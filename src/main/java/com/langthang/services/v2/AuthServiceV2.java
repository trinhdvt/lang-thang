package com.langthang.services.v2;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.langthang.exception.BadRequestError;
import com.langthang.exception.HttpError;
import com.langthang.exception.InternalError;
import com.langthang.exception.UnauthorizedError;
import com.langthang.model.constraints.Role;
import com.langthang.model.dto.v2.request.GoogleLoginCredential;
import com.langthang.model.dto.v2.request.LoginCredential;
import com.langthang.model.entity.Account;
import com.langthang.repository.AccountRepository;
import com.langthang.security.services.CurrentUser;
import com.langthang.security.services.TokenServices;
import com.langthang.services.IAuthServices;
import com.langthang.specification.AccountSpec;
import com.langthang.utils.MyStringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AuthServiceV2 {

    private final IAuthServices authServicesV1;
    private final AuthenticationManager authManager;
    private final TokenServices tokenService;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final AccountRepository userRepo;
    private final TokenServices jwtServices;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.jwt.refresh-token.cookie-name}")
    private String REFRESH_TOKEN_COOKIE_NAME;

    @Value("${security.jwt.refresh-token.cookie-length}")
    private int REFRESH_TOKEN_COOKIE_LENGTH;

    public String login(LoginCredential loginCredential, HttpServletResponse resp) {
        var email = loginCredential.email();
        var password = loginCredential.password();
        try {

            var authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            var authUser = (CurrentUser) authentication.getPrincipal();
            String token = tokenService.createToken(authUser.getSource());
            authServicesV1.addRefreshTokenCookie(email, token, resp);

            return token;
        } catch (DisabledException ex) {
            throw new HttpError("Account is not verified!", HttpStatus.LOCKED);
        } catch (AuthenticationException e) {
            throw new UnauthorizedError("Invalid email / password");
        }
    }

    public String loginWithGoogle(GoogleLoginCredential credential, HttpServletResponse resp) {
        try {
            var payload = Optional.of(googleIdTokenVerifier.verify(credential.credential()))
                    .orElseThrow(() -> new InternalError("Verify Google Token failed"))
                    .getPayload();

            String email = payload.getEmail();
            var user = userRepo.findOne(AccountSpec.hasEmail(email))
                    .orElseGet(() -> Account.builder()
                            .email(email)
                            .name(payload.get("name").toString())
                            .avatarLink(payload.get("picture").toString())
                            .password(passwordEncoder.encode(MyStringUtils.randomID(10)))
                            .enabled(true)
                            .role(Role.ROLE_USER)
                            .build());
            user.setEnabled(true);
            user = userRepo.save(user);

            String accessToken = jwtServices.createToken(user);
            addRefreshTokenCookie(email, accessToken, resp);
            return accessToken;

        } catch (GeneralSecurityException | IOException e) {
            throw new BadRequestError("Invalid Google Token");
        }
    }

    public void addRefreshTokenCookie(String email, String accessToken, HttpServletResponse resp) {
        String refreshToken = tokenService.createRefreshToken(email, accessToken);

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(REFRESH_TOKEN_COOKIE_LENGTH); // ms -> s
        cookie.setPath("/");
        cookie.setDomain("trinhdvt.tech");
        resp.addCookie(cookie);
    }
}
