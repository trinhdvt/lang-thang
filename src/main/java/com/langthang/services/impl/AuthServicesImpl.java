package com.langthang.services.impl;

import com.langthang.dto.UserDTO;
import com.langthang.exception.CustomException;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.PasswordResetToken;
import com.langthang.model.entity.RegisterToken;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.PasswordResetTokenRepository;
import com.langthang.repository.RegisterTokenRepository;
import com.langthang.services.IAuthServices;
import com.langthang.services.JwtTokenServices;
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
import java.util.Calendar;
import java.util.UUID;

@Service
@Transactional
public class AuthServicesImpl implements IAuthServices {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtTokenServices jwtTokenServices;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private RegisterTokenRepository tokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String signIn(String email, String password, HttpServletResponse resp) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            String jwtToken = jwtTokenServices.createToken(email,
                    accountRepository.findByEmailAndEnabled(email, true).getRole());

            jwtTokenServices.addRefreshTokenCookie(email, resp);

            return jwtToken;

        } catch (AuthenticationException e) {
            throw new CustomException(e.getMessage()
                    , HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }


    @Override
    public String refreshToken(String clientToken, HttpServletRequest req, HttpServletResponse resp) {
        String email = req.getRemoteUser();

        if (jwtTokenServices.isValidRefreshToken(email, clientToken)) {
            jwtTokenServices.addRefreshTokenCookie(email, resp);

            return jwtTokenServices.createToken(email,
                    accountRepository.findByEmail(email).getRole());
        } else {
            throw new CustomException("Invalid refresh token!"
                    , HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Override
    public Account registerNewAccount(UserDTO userDTO) {
        if (isEmailExist(userDTO.getEmail())) {
            throw new CustomException("There is an account with that email address: " + userDTO.getEmail()
                    , HttpStatus.CONFLICT);
        }

        Account account = new Account();
        account.setName(userDTO.getName());
        account.setEmail(userDTO.getEmail());
        account.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        return accountRepository.save(account);
    }

    @Override
    public String createVerifyToken(Account account) {
        String token = UUID.randomUUID().toString();
        RegisterToken myToken = new RegisterToken(token, account);
        tokenRepository.save(myToken);
        return token;
    }

    @Override
    public RegisterToken generateNewRegisterToken(String existToken) {
        RegisterToken oldToken = tokenRepository.findByToken(existToken);
        String newToken = UUID.randomUUID().toString();
        oldToken.updateToken(newToken);

        return tokenRepository.save(oldToken);
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
        return accountRepository.findByEmail(email);
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
    public void changeAccountPassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    private boolean isEmailExist(String email) {
        return accountRepository.findByEmail(email) != null;
    }
}
