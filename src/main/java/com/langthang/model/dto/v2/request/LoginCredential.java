package com.langthang.model.dto.v2.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record LoginCredential(
        @Email(message = "Email is not valid", regexp = "^[A-Za-z0-9+_.-]+@(.+)$")
        String email,
        @Size(min = 6, max = 32, message = "Password must be between 6 and 32 characters")
        String password
) {
}
