package com.langthang.controller;

import com.langthang.annotation.PasswordMatches;
import com.langthang.annotation.ValidEmail;
import com.langthang.dto.AccountRegisterDTO;
import com.langthang.dto.JwtTokenDTO;
import com.langthang.dto.PasswordDTO;
import com.langthang.model.Account;
import com.langthang.services.IAuthServices;
import com.langthang.utils.MyMailSender;
import com.langthang.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Validated
@RestController
public class AuthenticationController {

    @Value("${security.jwt.token.expire-length}")
    private int TOKEN_EXPIRE_TIME;

    private final IAuthServices authServices;

    private final MyMailSender mailSender;

    @PostMapping("/auth/login")
    public ResponseEntity<Object> login(
            @RequestParam("email") @ValidEmail String email,
            @RequestParam("password") String password,
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

        if (!accessToken.startsWith("Bearer"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid authorization header");

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

        String resetPasswordToken = authServices.createPasswordResetToken(email);

        String confirmUrl = Utils.getAppUrl() + "/auth/resetPassword/" + resetPasswordToken;
        mailSender.sendResetPasswordEmail(email, confirmUrl);

        return ResponseEntity.accepted().build();
    }

    @GetMapping(value = "/auth/changePassword", params = {"token"})
    public ResponseEntity<Object> verifyResetPasswordToken(
            @RequestParam("token") String token) {

        authServices.validatePasswordResetToken(token);

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/auth/savePassword")
    public ResponseEntity<Object> savePassword(
            @RequestParam("token") @NotBlank String token,
            @Valid @PasswordMatches PasswordDTO passwordDTO) {

        Account account = authServices.getAccountAndRemovePwdToken(token);

        authServices.updatePasswordAndSave(account, passwordDTO.getPassword());

        return ResponseEntity.accepted().build();
    }

}
