package com.langthang.controller.v2.definition;

import com.langthang.model.dto.request.AccountRegisterDTO;
import com.langthang.model.dto.v2.request.GoogleLoginCredential;
import com.langthang.model.dto.v2.request.LoginCredential;
import com.langthang.model.dto.v2.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Authentication API", description = "API for authentication")
public interface AuthAPI {
    @Operation(summary = "Login with email and password")
    @PostMapping("/auth/login")
    LoginResponse login(@Valid @RequestBody LoginCredential loginCredential, HttpServletResponse resp);

    @Operation(summary = "Register new account with email and password")
    @PostMapping("/auth/register")
    ResponseEntity<Object> register(@Valid @RequestBody AccountRegisterDTO accountRegisterDTO);

    @Operation(summary = "Login/Register with Google account")
    @PostMapping("/auth/google")
    LoginResponse loginWithGoogle(@Valid @RequestBody GoogleLoginCredential credential, HttpServletResponse response);
}
