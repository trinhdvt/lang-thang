package com.langthang.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class ResetPasswordDTO {
    @NonNull
    @NotEmpty
    private String newPassword;

    @NonNull
    @NotEmpty
    private String matchedPassword;

    @NonNull
    @NotEmpty
    private String token;
}
