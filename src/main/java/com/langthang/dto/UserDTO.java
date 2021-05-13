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
public class UserDTO {
    @NotNull
    @NotEmpty
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
    private String email;
}
