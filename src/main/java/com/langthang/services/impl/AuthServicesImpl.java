package com.langthang.services.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.langthang.exception.HttpError;
import com.langthang.exception.NotFoundError;
import com.langthang.exception.UnauthorizedError;
import com.langthang.model.constraints.Role;
import com.langthang.model.dto.request.AccountRegisterDTO;
import com.langthang.model.dto.request.PasswordDTO;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.PasswordResetToken;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.PasswordResetTokenRepository;
import com.langthang.security.services.TokenServices;
import com.langthang.services.IAuthServices;
import com.langthang.utils.AssertUtils;
import com.langthang.utils.MyMailSender;
import com.langthang.utils.MyStringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Optional;

import static com.langthang.specification.AccountSpec.hasRegisterToken;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
@Transactional
public class AuthServicesImpl implements IAuthServices {

    private final AccountRepository accountRepository;

    private final TokenServices jwtTokenServices;

    private final AuthenticationManager authManager;

    private final PasswordResetTokenRepository pwdResetTokenRepo;

    private final PasswordEncoder passwordEncoder;

    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    private final MyMailSender mailSender;

    @Value("${security.jwt.refresh-token.cookie-name}")
    private String REFRESH_TOKEN_COOKIE_NAME;

    @Value("${security.jwt.refresh-token.cookie-length}")
    private int REFRESH_TOKEN_COOKIE_LENGTH;

    @Value("${application.server.url}")
    private String APP_DOMAIN;

    @Override
    public String login(String email, String password, HttpServletResponse resp) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            Account acc = accountRepository.getByEmail(email);
            String accessToken = jwtTokenServices.createAccessToken(acc);
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
            GoogleIdToken googleIdToken = Optional.of(googleIdTokenVerifier.verify(idToken))
                    .orElseThrow(() -> new HttpError("Verify Google Token failed",
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    );


            Payload payload = googleIdToken.getPayload();
            String email = payload.getEmail();

            // checking if account is already exists
            Account account = accountRepository.getByEmail(email);

            if (account != null) {
                //  if account is already exists but not activated yet
                if (!account.isEnabled()) {
                    account.setEnabled(true);
                    accountRepository.saveAndFlush(account);
                }

            } else {
                // account is not existed
                // create an account with random password
                String rawPassword = RandomStringUtils.randomAlphanumeric(10);
                account = googleProfileToAccount(payload);
                account.setPassword(passwordEncoder.encode(rawPassword));
                accountRepository.saveAndFlush(account);

                // send account's info back to user
                mailSender.sendCreatedAccountEmail(email, rawPassword);
            }

            // create access token and refresh-token cookie as well
            String accessToken = jwtTokenServices.createAccessToken(account);
            addRefreshTokenCookie(email, accessToken, resp);
            return accessToken;

        } catch (GeneralSecurityException | IOException e) {
            throw new UnauthorizedError("Invalid Google Token");
        }
    }

    @Override
    public String reCreateToken(String refreshToken, String accessToken, HttpServletResponse resp) {
        String email = jwtTokenServices.getUserName(accessToken);
        boolean isAble = jwtTokenServices.isValidToCreateNewAccessToken(email, refreshToken, accessToken);

        AssertUtils.isTrue(isAble, new UnauthorizedError("Unable to create new access token"));

        Account acc = accountRepository.getByEmail(email);
        String newAccessToken = jwtTokenServices.createAccessToken(acc);
        addRefreshTokenCookie(email, newAccessToken, resp);
        return newAccessToken;
    }

    @Override
    public void registerAccount(AccountRegisterDTO registerDTO) {
        String registerEmail = registerDTO.getEmail();

        // check if email is already registered
        Account existAcc = accountRepository.getByEmail(registerEmail);

        // if email is already registered
        if (existAcc != null) {

            // if email is activated
            AssertUtils.isTrue(!existAcc.isEnabled(), new HttpError("Email already existed", HttpStatus.CONFLICT));

            // if email isn't activated yet then re-send the activation link
            String registerToken = existAcc.getRegisterToken();
            String activationLink = APP_DOMAIN + "/auth/active/" + registerToken;
            mailSender.sendRegisterTokenEmail(existAcc.getEmail(), activationLink);

            // send back an error to warning client
            throw new HttpError("Please check your email to verify your account!"
                    , HttpStatus.LOCKED);
        }

        // if email is not in-use, then attempting to create new account
        Account newAccount = Account.builder()
                .name(registerDTO.getName())
                .email(registerDTO.getEmail())
                .role(Role.ROLE_USER)
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .registerToken(MyStringUtils.randomUUID())
                .build();
        newAccount = accountRepository.saveAndFlush(newAccount);

        // send an activation link
        String activationLink = APP_DOMAIN + "/auth/active/" + newAccount.getRegisterToken();
        mailSender.sendRegisterTokenEmail(newAccount.getEmail(), activationLink);
    }

    @Override
    public void validateRegisterToken(String token) {
        accountRepository.findOne(hasRegisterToken(token))
                .ifPresentOrElse(acc -> {
                    acc.setEnabled(true);
                    acc.setRegisterToken(null);
                    accountRepository.saveAndFlush(acc);
                }, () -> {
                    throw new UnauthorizedError("Invalid token");
                });
    }

    @Override
    public void createPasswordResetToken(String email) {
        Account account = accountRepository.getByEmail(email);

        // assert account is not null and already activated
        AssertUtils.notNull(account, new NotFoundError("Email not found!"));
        AssertUtils.isTrue(account.isEnabled(), new HttpError("Account not activated yet!", HttpStatus.LOCKED));

        // attempting to create pwd reset token
        // looking for existing token
        PasswordResetToken pwdResetToken = pwdResetTokenRepo.findByAccount(account);
        if (pwdResetToken == null) {

            // if not, then create a new one
            String token = MyStringUtils.randomUUID();
            pwdResetToken = new PasswordResetToken(token, account);
        } else {

            // else refresh its expiry time
            pwdResetToken.refreshExpiration();
        }

        // save it
        pwdResetToken = pwdResetTokenRepo.save(pwdResetToken);

        // send via email
        String pwdResetUrl = APP_DOMAIN + "/auth/resetPassword/" + pwdResetToken.getToken();
        mailSender.sendResetPasswordEmail(email, pwdResetUrl);
    }

    @Override
    public void validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = pwdResetTokenRepo.findByToken(token);

        AssertUtils.notNull(resetToken, new UnauthorizedError("Invalid token"));

        if (resetToken.getExpireDate().before(Calendar.getInstance().getTime())) {
            pwdResetTokenRepo.delete(resetToken);
            throw new HttpError("Token expired", HttpStatus.GONE);
        }
    }

    @Override
    public void resetPassword(String token, PasswordDTO passwordDTO) {
        // checking pwd reset token
        // if failed, exception will be thrown
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

    public void addRefreshTokenCookie(String email, String accessToken, HttpServletResponse resp) {
        String refreshToken = jwtTokenServices.createRefreshToken(email, accessToken);

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(REFRESH_TOKEN_COOKIE_LENGTH); // ms -> s
        cookie.setPath("/");
        cookie.setDomain("trinhdvt.tech");
        resp.addCookie(cookie);
    }
}
