package com.langthang.controller;

import com.langthang.services.LoginServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class LoginController {

    private final LoginServices loginServices;

    @Autowired
    public LoginController(LoginServices loginServices) {
        this.loginServices = loginServices;
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpServletResponse resp) {

        return loginServices.signIn(email, password, resp);
    }

    @PostMapping("/refresh")
    public String refreshToken(
            @CookieValue(name = "refresh-token", defaultValue = "") String clientToken,
            HttpServletRequest req,
            HttpServletResponse resp) {

        return loginServices.refreshToken(clientToken, req, resp);
    }

}
