package com.langthang.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class ResetPasswordDTO {
    @NonNull
    @NotEmpty
    @Size(min = 6, message = "Password must contain at least 6 character")
    private String password;

    @NonNull
    @NotEmpty
    @Size(min = 6, message = "Password must contain at least 6 character")
    private String matchedPassword;

    @NonNull
    @NotEmpty
    private String token;
}
