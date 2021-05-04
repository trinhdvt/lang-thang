package com.langthang.dto;


import com.langthang.annotation.PasswordMatches;
import com.langthang.annotation.ValidEmail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class UserDTO {
    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String password;
    private String matchedPassword;

    @NotNull
    @NotEmpty
    @ValidEmail
    private String email;
}
