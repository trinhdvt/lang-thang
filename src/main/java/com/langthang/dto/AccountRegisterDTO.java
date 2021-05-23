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
    @Size(max = 50, message = "Name's length cannot exceed 50 character")
    private String name;

    @NotNull
    @NotEmpty
    @Size(min = 6, message = "Password must contain at least 6 character")
    private String password;

    @NotEmpty
    @NotNull
    @Size(min = 6)
    private String matchedPassword;

    @NotNull
    @NotEmpty
    @ValidEmail
    @Size(max = 255, message = "Email's length cannot exceed 100 character")
    private String email;
}
