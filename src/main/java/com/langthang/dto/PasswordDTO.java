package com.langthang.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class PasswordDTO {

    @NonNull
    @NotEmpty
    @Size(min = 6, message = "Password must contain at least 6 character")
    private String password;

    @NonNull
    @NotEmpty
    @Size(min = 6, message = "Password must contain at least 6 character")
    private String matchedPassword;
}
