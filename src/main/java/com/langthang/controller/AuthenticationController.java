package com.langthang.controller;

import com.langthang.annotation.PasswordMatches;
import com.langthang.annotation.ValidEmail;
import com.langthang.dto.AccountRegisterDTO;
import com.langthang.dto.JwtTokenDTO;
import com.langthang.dto.PasswordDTO;
import com.langthang.event.OnRegisterWithGoogle;
import com.langthang.event.OnRegistrationEvent;
import com.langthang.event.OnResetPasswordEvent;
import com.langthang.model.Account;
import com.langthang.model.RegisterToken;
import com.langthang.services.IAuthServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
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

    private final ApplicationEventPublisher eventPublisher;

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

        Account tmpAcc = authServices.createAccountUseGoogleToken(googleToken);

        Account existingAcc = authServices.findAccountByEmail(tmpAcc.getEmail());

        if (existingAcc != null) {
            if (!existingAcc.isEnabled()) {
                return new ResponseEntity<>("Please check your email to verify your account!",
                        HttpStatus.UNAUTHORIZED);
            } else
                return login(existingAcc.getEmail(), null, resp);
        } else {
            eventPublisher.publishEvent(new OnRegisterWithGoogle(tmpAcc, tmpAcc.getPassword()));

            Account savedAcc = authServices.saveCreatedGoogleAccount(tmpAcc);

            return login(savedAcc.getEmail(), null, resp);
        }
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

        String registrationUrl = CLIENT_BASE_URL + "/auth/active/";
        eventPublisher.publishEvent(new OnRegistrationEvent(account, registrationUrl));

        return ResponseEntity.accepted().build();
    }

    @PostMapping(value = "/auth/registrationConfirm")
    public ResponseEntity<Object> confirmRegistration(
            @RequestParam("token") String token) {

        authServices.validateRegisterToken(token);

        return ResponseEntity.accepted().build();
    }

    @GetMapping(value = "/auth/resendRegistrationToken", params = {"token"})
    public ResponseEntity<Object> resendRegistrationToken(
            @RequestParam("token") String existToken,
            HttpServletRequest req) {

        RegisterToken newToken = authServices.generateNewRegisterToken(existToken);
        eventPublisher.publishEvent(new OnRegistrationEvent(newToken.getAccount(),
                getAppUrl(req) + "/registrationConfirm?token=",
                newToken.getToken()));

        return ResponseEntity.accepted().build();
    }

    @PostMapping("/auth/resetPassword")
    public ResponseEntity<Object> resetPassword(
            @RequestParam("email") @ValidEmail String email) {

        Account account = authServices.findAccountByEmail(email);

        if (account == null) {
            return new ResponseEntity<>("Email not found", HttpStatus.NOT_FOUND);
        }

        String resetPasswordUrl = CLIENT_BASE_URL + "/auth/resetPassword/";

        eventPublisher.publishEvent(new OnResetPasswordEvent(account, resetPasswordUrl));

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

        authServices.changeAccountPassword(account, passwordDTO.getPassword());

        return ResponseEntity.accepted().build();
    }

    /*----------------NON-API----------------*/
    private String getAppUrl(HttpServletRequest req) {
        return "http://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
    }

}
