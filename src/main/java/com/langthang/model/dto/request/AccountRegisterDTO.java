package com.langthang.model.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRegisterDTO {
    @NotEmpty
    @Size(max = 50, message = "Name's length cannot exceed 50 characters")
    private String name;

    @NotEmpty
    @Size(min = 6, message = "Password must contain at least 6 characters")
    @Size(max = 32, message = "Password can't exceed 32 characters")
    private String password;

    @NotEmpty
    private String passwordConfirm;

    @NotEmpty
    @Email(
            regexp = "^[A-Za-z0-9+_.-]+@(.+)$",
            message = "Email is not valid"
    )
    @Size(max = 100, message = "Email's length cannot exceed 100 characters")
    private String email;
}