package com.langthang.controller;

import com.langthang.annotation.ValidEmail;
import com.langthang.dto.JwtDTO;
import com.langthang.dto.ResetPasswordDTO;
import com.langthang.dto.UserDTO;
import com.langthang.event.OnRegisterWithGoogle;
import com.langthang.event.OnRegistrationEvent;
import com.langthang.event.OnResetPasswordEvent;
import com.langthang.exception.CustomException;
import com.langthang.exception.CustomResponse;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.RegisterToken;
import com.langthang.services.IAuthServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthenticationController {

    private static final String BASE_URL = "/auth";

    @Autowired
    private IAuthServices authServices;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/login")
    public ResponseEntity<Object> login(
            @RequestParam("email") @ValidEmail String email,
            @RequestParam("password") String password,
            HttpServletResponse resp) {
        log.info("Begin of login");

        String jwtToken = authServices.login(email, password, resp);

        log.info("End of login");
        return ResponseEntity.ok(new JwtDTO(jwtToken));
    }

    @PostMapping("/google")
    public ResponseEntity<Object> loginWithGoogle(
            @RequestParam("google_token")
            @NotBlank String googleToken
            , HttpServletResponse resp) {
        log.info("Begin of login with Google");

        Account tmpAcc = authServices.createAccountUseGoogleToken(googleToken);

        Account existingAcc = authServices.findAccountByEmail(tmpAcc.getEmail());

        if (existingAcc != null) {

            log.info("End of login with Google");
            return login(existingAcc.getEmail(), null, resp);
        } else {
            eventPublisher.publishEvent(new OnRegisterWithGoogle(tmpAcc, tmpAcc.getPassword()));

            Account savedAcc = authServices.saveCreatedGoogleAccount(tmpAcc);

            log.info("End of login with Google");
            return login(savedAcc.getEmail(), null, resp);
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<Object> refreshToken(
            @CookieValue(name = "refresh-token", defaultValue = "")
            @NotBlank String clientToken,
            HttpServletRequest req,
            HttpServletResponse resp) {
        log.info("Begin of refresh token");
        String newJwtToken = authServices.refreshToken(clientToken, req, resp);
        log.info("End of refresh token");
        return new ResponseEntity<>(newJwtToken, HttpStatus.OK);
    }

    @PostMapping("/registration")
    public ResponseEntity<Object> register(
            @Valid UserDTO userDTO,
            HttpServletRequest req) {
        log.info("Begin of registration");

        Account account = authServices.registerNewAccount(userDTO);
        eventPublisher.publishEvent(new OnRegistrationEvent(account, getAppUrl(req)));

        log.info("End of registration");
        return ResponseEntity.ok(new CustomResponse("OK", HttpStatus.OK.value()));
    }

    @GetMapping("/registrationConfirm")
    public ModelAndView confirmRegistration(
            @RequestParam("token") String token,
            HttpServletRequest req) {

        try {
            authServices.validateRegisterToken(token);
        } catch (CustomException ex) {
            Map<String, String> modelMap = new HashMap<>();
            modelMap.put("message", ex.getMessage());
            if (ex.getHttpStatus() == HttpStatus.GONE) {
                modelMap.put("token", token);
                modelMap.put("link", getAppUrl(req) + "/resendRegistrationToken");
            }
            return new ModelAndView("registration-error", modelMap);
        }

        return new ModelAndView("registration-done");
    }

    @GetMapping("/resendRegistrationToken")
    public ResponseEntity<Object> resendRegistrationToken(
            @RequestParam("token") String existToken,
            HttpServletRequest req) {

        RegisterToken newToken = authServices.generateNewRegisterToken(existToken);
        eventPublisher.publishEvent(new OnRegistrationEvent(newToken.getAccount(), getAppUrl(req), newToken.getToken()));

        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Object> resetPassword(
            @RequestParam("email") @ValidEmail String email,
            HttpServletRequest req) {

        Account account = authServices.findAccountByEmail(email);

        if (account == null) {
            return new ResponseEntity<>("Email not found", HttpStatus.FORBIDDEN);
        }
        eventPublisher.publishEvent(new OnResetPasswordEvent(account, getAppUrl(req)));

        return ResponseEntity.ok(new CustomResponse("OK", HttpStatus.OK.value()));
    }

    @GetMapping("/changePassword")
    public ResponseEntity<Object> verifyResetPasswordToken(@RequestParam("token") String token) {
        authServices.validatePasswordResetToken(token);

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PostMapping("/savePassword")
    public ResponseEntity<Object> savePassword(@Valid ResetPasswordDTO resetPasswordDTO) {
        authServices.validatePasswordResetToken(resetPasswordDTO.getToken());

        Account account = authServices.findAccountByPasswordResetToken(resetPasswordDTO.getToken());
        if (account != null) {
            authServices.changeAccountPassword(account, resetPasswordDTO.getNewPassword());
            return new ResponseEntity<>("Password has changed", HttpStatus.OK);
        }

        return new ResponseEntity<>("Account not found", HttpStatus.FORBIDDEN);
    }

    /*----------------NON-API----------------*/
    private String getAppUrl(HttpServletRequest req) {
        return "http://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + BASE_URL;
    }

}
