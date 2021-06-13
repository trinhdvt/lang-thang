package com.langthang.controller;

import com.langthang.annotation.PasswordMatches;
import com.langthang.annotation.ValidEmail;
import com.langthang.dto.AccountRegisterDTO;
import com.langthang.dto.JwtTokenDTO;
import com.langthang.dto.PasswordDTO;
import com.langthang.exception.CustomException;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Validated
@RestController
public class AuthenticationController {

    private static final String CLIENT_BASE_URL = "http://localhost:3000";

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

        Account account = authServices.createAccountUseGoogleToken(googleToken);

        if (Utils.isEmpty(account.getPassword())) {
            String randomPassword = Utils.randomString(10);
            authServices.updatePasswordAndSave(account, randomPassword);
            mailSender.sendCreatedAccountEmail(account.getEmail(), randomPassword);
        }

        return login(account.getEmail(), null, resp);
    }

    @PostMapping("/auth/refreshToken")
    public ResponseEntity<Object> refreshToken(
            @CookieValue(name = "refresh-token", defaultValue = "")
            @NotBlank String refreshToken,
            HttpServletRequest req,
            HttpServletResponse resp) {

        String newAccessToken = authServices.refreshToken(refreshToken, req, resp);

        return ResponseEntity.ok(new JwtTokenDTO(newAccessToken, TOKEN_EXPIRE_TIME));
    }

    @PostMapping("/auth/registration")
    public ResponseEntity<Object> register(
            @Valid @PasswordMatches AccountRegisterDTO accountRegisterDTO) {

        Account account = authServices.registerNewAccount(accountRegisterDTO);
        boolean isExistButNotActive = account.getRegisterToken() != null;

        String registrationToken = authServices.createRegistrationToken(account);

        String confirmUrl = CLIENT_BASE_URL + "/auth/active/" + registrationToken;
        mailSender.sendRegisterTokenEmail(account.getEmail(), confirmUrl);

        if (isExistButNotActive) {
            throw new CustomException("Please check your email to verify your account!"
                    , HttpStatus.FORBIDDEN);
        }

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

        String confirmUrl = CLIENT_BASE_URL + "/auth/resetPassword/" + resetPasswordToken;
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

        authServices.validatePasswordResetToken(token);

        Account account = authServices.findAccountByPasswordResetToken(token);

        if (account == null) {
            return new ResponseEntity<>("Account not found", HttpStatus.FORBIDDEN);
        }

        authServices.updatePasswordAndSave(account, passwordDTO.getPassword());

        return ResponseEntity.accepted().build();
    }

}
