package com.langthang.controller.v1;

import com.langthang.annotation.PasswordMatches;
import com.langthang.annotation.ValidEmail;
import com.langthang.model.dto.request.AccountRegisterDTO;
import com.langthang.model.dto.response.JwtTokenDTO;
import com.langthang.model.dto.request.PasswordDTO;
import com.langthang.exception.UnauthorizedError;
import com.langthang.services.IAuthServices;
import com.langthang.utils.AssertUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Validated
@RestController
public class AuthenticationController {

    private final IAuthServices authServices;
    @Value("${security.jwt.token.expire-length}")
    private int TOKEN_EXPIRE_TIME;

    @PostMapping("/auth/login")
    public ResponseEntity<Object> login(
            @RequestParam("email") @ValidEmail String email,
            @RequestParam("password") @Size(min = 6, max = 32) String password,
            HttpServletResponse resp) {

        String jwtToken = authServices.login(email, password, resp);

        return ResponseEntity.ok(new JwtTokenDTO(jwtToken, TOKEN_EXPIRE_TIME));
    }

    @PostMapping("/auth/google")
    public ResponseEntity<Object> loginWithGoogle(
            @RequestParam("google_token")
            @NotBlank String googleToken
            , HttpServletResponse resp) {

        String accessToken = authServices.loginWithGoogle(googleToken, resp);

        return ResponseEntity.ok(new JwtTokenDTO(accessToken, TOKEN_EXPIRE_TIME));
    }

    @PostMapping("/auth/refreshToken")
    public ResponseEntity<Object> refreshToken(
            @CookieValue(name = "refresh-token") String refreshToken,
            @RequestHeader("Authorization") String accessToken,
            HttpServletResponse resp) {

        AssertUtils.isTrue(accessToken.startsWith("Bearer"),
                new UnauthorizedError("Invalid authorization header"));

        String newAccessToken = authServices.reCreateToken(refreshToken, accessToken.substring(7), resp);

        return ResponseEntity.ok(new JwtTokenDTO(newAccessToken, TOKEN_EXPIRE_TIME));
    }

    @PostMapping("/auth/registration")
    public ResponseEntity<Object> register(
            @Valid @PasswordMatches AccountRegisterDTO accountRegisterDTO) {

        authServices.registerAccount(accountRegisterDTO);

        return ResponseEntity.accepted().build();
    }

    @PostMapping(value = "/auth/registrationConfirm")
    public ResponseEntity<Object> confirmRegistration(
            @RequestParam("token") String token) {

        authServices.validateRegisterToken(token);

        return ResponseEntity.accepted().build();
    }

    @PostMapping("/auth/resetPassword")
    public ResponseEntity<Object> resetPassword(
            @RequestParam("email") @ValidEmail String email) {

        authServices.createPasswordResetToken(email);

        return ResponseEntity.accepted().build();
    }

    @GetMapping(value = "/auth/resetPassword", params = {"token"})
    public ResponseEntity<Object> verifyResetPasswordToken(
            @RequestParam("token") String token) {

        authServices.validatePasswordResetToken(token);

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/auth/resetPassword")
    public ResponseEntity<Object> savePassword(
            @RequestParam("token") String token,
            @Valid @PasswordMatches PasswordDTO passwordDTO) {

        authServices.resetPassword(token, passwordDTO);

        return ResponseEntity.accepted().build();
    }

}