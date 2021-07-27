package com.langthang.dto;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class PasswordDTO {

    @NonNull
    @NotEmpty
    @Size(min = 6, message = "Password must contain at least 6 characters")
    @Size(max = 32, message = "Password can't exceed 32 characters")
    private String password;

    @NonNull
    @NotEmpty
    private String matchedPassword;
}
