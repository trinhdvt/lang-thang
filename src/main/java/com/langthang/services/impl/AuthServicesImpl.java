package com.langthang.services.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.langthang.dto.AccountRegisterDTO;
import com.langthang.dto.PasswordDTO;
import com.langthang.exception.HttpError;
import com.langthang.exception.NotFoundError;
import com.langthang.exception.UnauthorizedError;
import com.langthang.model.Account;
import com.langthang.model.PasswordResetToken;
import com.langthang.model.Role;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.PasswordResetTokenRepository;
import com.langthang.services.IAuthServices;
import com.langthang.services.JwtTokenServices;
import com.langthang.utils.MyMailSender;
import com.langthang.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
@Transactional
public class AuthServicesImpl implements IAuthServices {

    private final AccountRepository accountRepository;

    private final JwtTokenServices jwtTokenServices;

    private final AuthenticationManager authManager;

    private final PasswordResetTokenRepository pwdResetTokenRepo;

    private final PasswordEncoder passwordEncoder;

    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    private final MyMailSender mailSender;

    @Value("${security.jwt.refresh-token.cookie-name}")
    private String REFRESH_TOKEN_COOKIE_NAME;

    @Value("${security.jwt.refresh-token.cookie-length}")
    private int REFRESH_TOKEN_COOKIE_LENGTH;


    @Override
    public String login(String email, String password, HttpServletResponse resp) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            String accessToken = jwtTokenServices.createAccessToken(email);
            addRefreshTokenCookie(email, accessToken, resp);

            return accessToken;

        } catch (DisabledException ex) {
            throw new HttpError("Account is not verified!"
                    , HttpStatus.LOCKED);
        } catch (AuthenticationException e) {
            throw new UnauthorizedError("Invalid email / password");
        }
    }

    @Override
    public String loginWithGoogle(String idToken, HttpServletResponse resp) {
        try {
            GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
            if (googleIdToken != null) {
                Payload payload = googleIdToken.getPayload();
                String email = payload.getEmail();

                // checking if account is already exists
                Account account = accountRepository.findAccountByEmail(email);

                if (account != null) {

                    //  if account is already exists but not activated yet
                    if (!account.isEnabled()) {
                        account.setEnabled(true);
                        accountRepository.saveAndFlush(account);
                    }
                } else {
                    // account is not existed
                    // create an account with random password
                    String rawPassword = Utils.randomString(10);
                    account = googleProfileToAccount(payload);
                    account.setPassword(passwordEncoder.encode(rawPassword));
                    accountRepository.saveAndFlush(account);

                    // send account's info back to user
                    mailSender.sendCreatedAccountEmail(email, rawPassword);
                }

                // create access token and refresh-token cookie as well
                String accessToken = jwtTokenServices.createAccessToken(email);
                addRefreshTokenCookie(email, accessToken, resp);

                return accessToken;
            } else {
                throw new HttpError("Verify Google Token failed", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new UnauthorizedError("Invalid Google Token");
        }
    }

    @Override
    public String reCreateToken(String refreshToken, String accessToken, HttpServletResponse resp) {
        String email = jwtTokenServices.getUserName(accessToken);

        if (jwtTokenServices.isValidToCreateNewAccessToken(email, refreshToken, accessToken)) {
            String newAccessToken = jwtTokenServices.createAccessToken(email);
            addRefreshTokenCookie(email, newAccessToken, resp);

            return newAccessToken;
        } else {
            throw new UnauthorizedError("Unable to create new access token");
        }
    }

    @Override
    public void registerAccount(AccountRegisterDTO registerDTO) {
        String registerEmail = registerDTO.getEmail();

        // check if email is already registered
        Account existAcc = accountRepository.findAccountByEmail(registerEmail);

        // if email is already registered
        if (existAcc != null) {

            // if email is activated
            if (existAcc.isEnabled()) {
                throw new HttpError("Email already existed: " + registerEmail
                        , HttpStatus.CONFLICT);

            } else if (!existAcc.isEnabled()) {

                // if email isn't activated yet then re-send the activation link
                String registerToken = existAcc.getRegisterToken();
                String activationLink = Utils.getAppUrl() + "/auth/active/" + registerToken;
                mailSender.sendRegisterTokenEmail(existAcc.getEmail(), activationLink);

                // send back an error to warning client
                throw new HttpError("Please check your email to verify your account!"
                        , HttpStatus.LOCKED);
            }
        }

        // if email is not in-use, then attempting to create new account
        Account newAccount = Account.builder()
                .name(registerDTO.getName())
                .email(registerDTO.getEmail())
                .role(Role.ROLE_USER)
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .registerToken(Utils.randomUUID())
                .build();
        newAccount = accountRepository.saveAndFlush(newAccount);

        // send an activation link
        String activationLink = Utils.getAppUrl() + "/auth/active/" + newAccount.getRegisterToken();
        mailSender.sendRegisterTokenEmail(newAccount.getEmail(), activationLink);
    }

    @Override
    public void validateRegisterToken(String token) {
        Account account = accountRepository.findAccountByRegisterToken(token);

        if (account == null) {
            throw new HttpError("Invalid token"
                    , HttpStatus.UNAUTHORIZED);
        }

        account.setEnabled(true);
        account.setRegisterToken(null);
        accountRepository.saveAndFlush(account);
    }

    @Override
    public void createPasswordResetToken(String email) {
        // checking email
        Account account = accountRepository.findAccountByEmail(email);

        // if account not found
        if (account == null) {
            throw new NotFoundError("Email not found!");
        }

        // if account not activated yet
        if (!account.isEnabled()) {
            throw new HttpError("Account is not verified!", HttpStatus.LOCKED);
        }

        // attempting to create pwd reset token
        // looking for existing token
        PasswordResetToken pwdResetToken = pwdResetTokenRepo.findByAccount(account);
        if (pwdResetToken == null) {

            // if not, then create a new one
            String token = Utils.randomUUID();
            pwdResetToken = new PasswordResetToken(token, account);
        } else {

            // else refresh its expiry time
            pwdResetToken.refreshExpiration();
        }

        // save it
        pwdResetToken = pwdResetTokenRepo.save(pwdResetToken);

        // send via email
        String pwdResetUrl = Utils.getAppUrl() + "/auth/resetPassword/" + pwdResetToken.getToken();
        mailSender.sendResetPasswordEmail(email, pwdResetUrl);
    }

    @Override
    public void validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = pwdResetTokenRepo.findByToken(token);

        if (resetToken == null) {
            throw new UnauthorizedError("Invalid password reset token");
        }

        if (resetToken.getExpireDate().before(Calendar.getInstance().getTime())) {
            pwdResetTokenRepo.delete(resetToken);
            throw new HttpError("Token expired", HttpStatus.GONE);
        }
    }

    @Override
    public void resetPassword(String token, PasswordDTO passwordDTO) {
        // checking pwd reset token
        // if fail, exception will be thrown
        validatePasswordResetToken(token);

        // no exception, eligible to reset password
        PasswordResetToken pwdResetToken = pwdResetTokenRepo.findByToken(token);

        // get account and change its password
        Account account = pwdResetToken.getAccount();
        account.setPassword(passwordEncoder.encode(passwordDTO.getPassword()));
        accountRepository.saveAndFlush(account);

        // delete token after done
        pwdResetTokenRepo.delete(pwdResetToken);
    }

    private Account googleProfileToAccount(Payload payload) {
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String avatarLink = (String) payload.get("picture");

        return Account.builder()
                .email(email)
                .name(name)
                .avatarLink(avatarLink)
                .role(Role.ROLE_USER)
                .enabled(true)
                .build();
    }

    private void addRefreshTokenCookie(String email, String accessToken, HttpServletResponse resp) {
        String refreshToken = jwtTokenServices.createRefreshToken(email, accessToken);

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(REFRESH_TOKEN_COOKIE_LENGTH); // ms -> s
        cookie.setPath("/");
        resp.addCookie(cookie);
    }
}
