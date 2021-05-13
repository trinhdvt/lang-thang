package com.langthang.controller;

import com.langthang.annotation.ValidEmail;
import com.langthang.dto.JwtDTO;
import com.langthang.dto.ResetPasswordDTO;
import com.langthang.dto.UserDTO;
import com.langthang.event.OnRegisterWithGoogle;
import com.langthang.event.OnRegistrationEvent;
import com.langthang.event.OnResetPasswordEvent;
import com.langthang.exception.CustomException;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.RegisterToken;
import com.langthang.services.IAuthServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@CrossOrigin(originPatterns = "*")
public class AuthenticationController {

    private static final String BASE_URL = "/auth";

    @Value("${security.jwt.token.expire-length}")
    private int TOKEN_EXPIRE_TIME;

    @Autowired
    private IAuthServices authServices;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/login")
    public ResponseEntity<Object> login(
            @RequestParam("email") @ValidEmail String email,
            @RequestParam("password") String password,
            HttpServletResponse resp) {

        String jwtToken = authServices.login(email, password, resp);

        return ResponseEntity.ok(new JwtDTO(jwtToken, TOKEN_EXPIRE_TIME));
    }

    @PostMapping("/google")
    public ResponseEntity<Object> loginWithGoogle(
            @RequestParam("google_token")
            @NotBlank String googleToken
            , HttpServletResponse resp) {

        Account tmpAcc = authServices.createAccountUseGoogleToken(googleToken);

        Account existingAcc = authServices.findAccountByEmail(tmpAcc.getEmail());

        if (existingAcc != null) {
            return login(existingAcc.getEmail(), null, resp);
        } else {
            eventPublisher.publishEvent(new OnRegisterWithGoogle(tmpAcc, tmpAcc.getPassword()));

            Account savedAcc = authServices.saveCreatedGoogleAccount(tmpAcc);

            return login(savedAcc.getEmail(), null, resp);
        }
    }

    @PostMapping("/refreshToken")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> refreshToken(
            @CookieValue(name = "refresh-token", defaultValue = "")
            @NotBlank String clientToken,
            HttpServletRequest req,
            HttpServletResponse resp) {

        String newJwtToken = authServices.refreshToken(clientToken, req, resp);

        return ResponseEntity.ok(new JwtDTO(newJwtToken, TOKEN_EXPIRE_TIME));
    }

    @PostMapping("/registration")
    public ResponseEntity<Object> register(
            @Valid UserDTO userDTO,
            HttpServletRequest req) {

        if (!userDTO.getPassword().equals(userDTO.getMatchedPassword())) {
            return ResponseEntity.badRequest().body("Password don't match");
        }

        Account account = authServices.registerNewAccount(userDTO);
        eventPublisher.publishEvent(new OnRegistrationEvent(account, getAppUrl(req)));

        return ResponseEntity.accepted().build();
    }

    @GetMapping(value = "/registrationConfirm",params = {"token"})
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

    @GetMapping(value = "/resendRegistrationToken", params = {"token"})
    public ResponseEntity<Object> resendRegistrationToken(
            @RequestParam("token") String existToken,
            HttpServletRequest req) {

        RegisterToken newToken = authServices.generateNewRegisterToken(existToken);
        eventPublisher.publishEvent(new OnRegistrationEvent(newToken.getAccount(),
                getAppUrl(req) + "/registrationConfirm?token=",
                newToken.getToken()));

        return ResponseEntity.accepted().build();
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Object> resetPassword(
            @RequestParam("email") @ValidEmail String email,
            HttpServletRequest req) {

        Account account = authServices.findAccountByEmail(email);

        if (account == null) {
            return new ResponseEntity<>("Email not found", HttpStatus.NOT_FOUND);
        }
        eventPublisher.publishEvent(new OnResetPasswordEvent(account
                , getAppUrl(req) + "/changePassword?token="));

        return ResponseEntity.accepted().build();
    }

    @GetMapping(value = "/changePassword",params = {"token"})
    public ResponseEntity<Object> verifyResetPasswordToken(
            @RequestParam("token") String token) {

        authServices.validatePasswordResetToken(token);

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/savePassword")
    public ResponseEntity<Object> savePassword(
            @Valid ResetPasswordDTO resetPasswordDTO) {

        if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getMatchedPassword())) {
            return ResponseEntity.badRequest().body("Password doesn't match");
        }

        authServices.validatePasswordResetToken(resetPasswordDTO.getToken());

        Account account = authServices.findAccountByPasswordResetToken(resetPasswordDTO.getToken());

        if (account == null) {
            return new ResponseEntity<>("Account not found", HttpStatus.FORBIDDEN);
        }

        authServices.changeAccountPassword(account, resetPasswordDTO.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    /*----------------NON-API----------------*/
    private String getAppUrl(HttpServletRequest req) {
        return "http://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + BASE_URL;
    }

}
