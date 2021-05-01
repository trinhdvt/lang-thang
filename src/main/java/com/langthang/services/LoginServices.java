package com.langthang.services;

import com.langthang.exception.CustomException;
import com.langthang.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class LoginServices {

    private final AccountRepository accRepo;
    private final JwtTokenServices jwtTokenServices;
    private final AuthenticationManager authManager;

    @Autowired
    public LoginServices(AccountRepository accRepo, JwtTokenServices jwtTokenServices, AuthenticationManager authManager) {
        this.accRepo = accRepo;
        this.jwtTokenServices = jwtTokenServices;
        this.authManager = authManager;
    }

    public String signIn(String email, String password, HttpServletResponse resp) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            jwtTokenServices.addRefreshTokenCookie(email, resp);
            return jwtTokenServices.createToken(email,
                    accRepo.findByEmailAndStatus(email, true).getRole());
        } catch (AuthenticationException e) {
            throw new CustomException("Invalid email/password"
                    , HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public String signUp() {
        return null;
    }


    public String refreshToken(String clientToken, HttpServletRequest req, HttpServletResponse resp) {
        String email = req.getRemoteUser();
        if (!clientToken.isEmpty() && jwtTokenServices.isValidRefreshToken(email, clientToken)) {
            jwtTokenServices.addRefreshTokenCookie(email, resp);
            return jwtTokenServices.createToken(email,
                    accRepo.findByEmailAndStatus(email, true).getRole());
        } else {
            throw new CustomException("Invalid refresh token!", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
