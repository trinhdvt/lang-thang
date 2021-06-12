package com.langthang.services.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.langthang.dto.AccountRegisterDTO;
import com.langthang.exception.CustomException;
import com.langthang.model.Account;
import com.langthang.model.PasswordResetToken;
import com.langthang.model.RegisterToken;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.PasswordResetTokenRepository;
import com.langthang.repository.RegisterTokenRepository;
import com.langthang.services.IAuthServices;
import com.langthang.services.JwtTokenServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
@Transactional
public class AuthServicesImpl implements IAuthServices {

    private final AccountRepository accountRepository;

    private final JwtTokenServices jwtTokenServices;

    private final AuthenticationManager authManager;

    private final RegisterTokenRepository tokenRepository;

    private final PasswordResetTokenRepository passwordResetTokenRepo;

    private final PasswordEncoder passwordEncoder;

    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    @Override
    public String login(String email, String password, HttpServletResponse resp) {
        try {

            // Password will be null when user log in with Google Account
            if (password != null) {
                authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            }

            String accessToken = jwtTokenServices.createAccessToken(email);
            jwtTokenServices.addRefreshTokenCookie(email, accessToken, resp);

            return accessToken;

        } catch (AuthenticationException e) {
            throw new CustomException("Invalid email / password"
                    , HttpStatus.UNAUTHORIZED);
        }
    }


    @Override
    public String refreshToken(String refreshToken, HttpServletRequest req, HttpServletResponse resp) {
        String accessToken = jwtTokenServices.getAccessToken(req);
        String email = jwtTokenServices.getUserName(accessToken);

        if (jwtTokenServices.isValidToCreateNewAccessToken(email, refreshToken, accessToken)) {
            String newAccessToken = jwtTokenServices.createAccessToken(email);

            jwtTokenServices.addRefreshTokenCookie(email, newAccessToken, resp);

            return newAccessToken;
        } else {
            throw new CustomException("Unable to create new access token"
                    , HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public Account registerNewAccount(AccountRegisterDTO accountRegisterDTO) {
        Account existAcc = accountRepository.findAccountByEmail(accountRegisterDTO.getEmail());

        if (existAcc != null) {
            if (existAcc.isEnabled()) {
                throw new CustomException("Email already existed: " + accountRegisterDTO.getEmail()
                        , HttpStatus.CONFLICT);
            } else if (!existAcc.isEnabled()) {
                throw new CustomException("Please check your email to verify your account!"
                        , HttpStatus.UNAUTHORIZED);
            }
        }

        Account account = new Account();
        account.setName(accountRegisterDTO.getName());
        account.setEmail(accountRegisterDTO.getEmail());
        account.setPassword(passwordEncoder.encode(accountRegisterDTO.getPassword()));

        return accountRepository.save(account);
    }

    @Override
    public String createRegistrationToken(Account account) {
        String token = UUID.randomUUID().toString();
        RegisterToken myToken = new RegisterToken(token, account);
        tokenRepository.save(myToken);
        return token;
    }

    @Override
    public void validateRegisterToken(String token) {
        RegisterToken registerToken = tokenRepository.findByToken(token);

        if (registerToken == null) {
            throw new CustomException("Invalid token"
                    , HttpStatus.FORBIDDEN);
        }

        if (registerToken.getExpireDate().before(Calendar.getInstance().getTime())) {
            throw new CustomException("Token expired"
                    , HttpStatus.GONE);
        }

        Account account = registerToken.getAccount();
        account.setEnabled(true);
        tokenRepository.delete(registerToken);
        accountRepository.save(account);
    }

    @Override
    public Account findAccountByEmail(String email) {
        return accountRepository.findAccountByEmail(email);
    }

    @Override
    public String createPasswordResetToken(Account account) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, account);
        passwordResetTokenRepo.save(resetToken);

        return token;
    }

    @Override
    public void validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepo.findByToken(token);

        if (resetToken == null) {
            throw new CustomException("Invalid password reset token"
                    , HttpStatus.FORBIDDEN);
        }

        if (resetToken.getExpireDate().before(Calendar.getInstance().getTime())) {
            throw new CustomException("Token expired", HttpStatus.GONE);
        }
    }

    @Override
    public Account findAccountByPasswordResetToken(String token) {
        return passwordResetTokenRepo.findByToken(token).getAccount();
    }

    @Override
    public void updatePasswordAndSave(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.saveAndFlush(account);
    }

    @Override
    public Account createAccountUseGoogleToken(String idToken) {
        try {
            GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
            if (googleIdToken != null) {
                Payload payload = googleIdToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String avatarLink = (String) payload.get("picture");
                Account createdAccount = Account.builder()
                        .email(email)
                        .name(name)
                        .avatarLink(avatarLink)
                        .enabled(true)
                        .build();

                Account existingAccount = accountRepository.findAccountByEmail(email);

                if (existingAccount != null) {
                    if (!existingAccount.isEnabled()) {
                        existingAccount.setEnabled(true);
                        return accountRepository.saveAndFlush(existingAccount);
                    }
                    return existingAccount;
                } else
                    return createdAccount;

            } else {
                throw new CustomException("Verify Google Token failed"
                        , HttpStatus.BAD_REQUEST);
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new CustomException("Invalid Google Token"
                    , HttpStatus.BAD_REQUEST);
        }
    }
}
