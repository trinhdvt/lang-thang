package com.langthang.dto;


import com.langthang.annotation.ValidEmail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRegisterDTO {
    @NotNull
    @NotEmpty
    @Size(max = 50, message = "Name's length cannot exceed 50 characters")
    private String name;

    @NotNull
    @NotEmpty
    @Size(min = 6, message = "Password must contain at least 6 characters")
    @Size(max = 32, message = "Password can't exceed 32 characters")
    private String password;

    @NotEmpty
    @NotNull
    private String matchedPassword;

    @NotNull
    @NotEmpty
    @ValidEmail
    @Size(max = 100, message = "Email's length cannot exceed 100 characters")
    private String email;
}
