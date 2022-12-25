package com.langthang.services.v2;

import com.langthang.exception.HttpError;
import com.langthang.exception.UnauthorizedError;
import com.langthang.model.dto.v2.request.LoginCredential;
import com.langthang.security.services.CurrentUser;
import com.langthang.security.services.TokenServices;
import com.langthang.services.IAuthServices;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AuthServiceV2 {

    private final IAuthServices authServicesV1;

    private final AuthenticationManager authManager;

    private final TokenServices tokenService;

    public String login(LoginCredential loginCredential, HttpServletResponse resp) {
        var email = loginCredential.email();
        var password = loginCredential.password();
        try {

            var authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            var authUser = (CurrentUser) authentication.getPrincipal();
            String token = tokenService.createToken(authUser);
            authServicesV1.addRefreshTokenCookie(email, token, resp);

            return token;
        } catch (DisabledException ex) {
            throw new HttpError("Account is not verified!", HttpStatus.LOCKED);
        } catch (AuthenticationException e) {
            throw new UnauthorizedError("Invalid email / password");
        }
    }
}
