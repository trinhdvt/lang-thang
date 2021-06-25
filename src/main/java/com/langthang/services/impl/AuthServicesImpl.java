package com.langthang.services.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.langthang.dto.AccountRegisterDTO;
import com.langthang.exception.CustomException;
import com.langthang.model.Account;
import com.langthang.model.PasswordResetToken;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.PasswordResetTokenRepository;
import com.langthang.services.IAuthServices;
import com.langthang.services.JwtTokenServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
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

        } catch (DisabledException ex) {
            throw new CustomException("Account is not verified!"
                    , HttpStatus.LOCKED);
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
                    , HttpStatus.UNAUTHORIZED);
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
                return existAcc;
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
        String existingToken = account.getRegisterToken();
        if (existingToken != null)
            return existingToken;

        String token = UUID.randomUUID().toString();
        account.setRegisterToken(token);
        accountRepository.saveAndFlush(account);
        return token;
    }

    @Override
    public void validateRegisterToken(String token) {
        Account account = accountRepository.findAccountByRegisterToken(token);

        if (account == null) {
            throw new CustomException("Invalid token"
                    , HttpStatus.UNAUTHORIZED);
        }

        account.setEnabled(true);
        account.setRegisterToken(null);
        accountRepository.saveAndFlush(account);
    }


    @Override
    public String createPasswordResetToken(String email) {
        Account account = accountRepository.findAccountByEmail(email);
        if (account == null) {
            throw new CustomException("Email not found!", HttpStatus.NOT_FOUND);
        }
        if (!account.isEnabled()) {
            throw new CustomException("Account is not verified!", HttpStatus.LOCKED);
        }

        PasswordResetToken existingToken = passwordResetTokenRepo.findByAccount(account);
        if (existingToken == null) {
            String token = UUID.randomUUID().toString();
            existingToken = new PasswordResetToken(token, account);
        } else {
            existingToken.refreshExpiration();
        }

        passwordResetTokenRepo.save(existingToken);
        return existingToken.getToken();
    }

    @Override
    public void validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepo.findByToken(token);

        if (resetToken == null) {
            throw new CustomException("Invalid password reset token"
                    , HttpStatus.UNAUTHORIZED);
        }

        if (resetToken.getExpireDate().before(Calendar.getInstance().getTime())) {
            passwordResetTokenRepo.delete(resetToken);
            throw new CustomException("Token expired", HttpStatus.GONE);
        }
    }

    @Override
    public Account getAccountAndRemovePwdToken(String token) {
        validatePasswordResetToken(token);
        PasswordResetToken pwdResetToken = passwordResetTokenRepo.findByToken(token);
        passwordResetTokenRepo.delete(pwdResetToken);

        return pwdResetToken.getAccount();
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
                        , HttpStatus.UNAUTHORIZED);
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new CustomException("Invalid Google Token"
                    , HttpStatus.UNAUTHORIZED);
        }
    }
}
