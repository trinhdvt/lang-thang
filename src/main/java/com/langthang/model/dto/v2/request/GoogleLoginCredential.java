package com.langthang.model.dto.v2.request;

import jakarta.validation.constraints.NotEmpty;

public record GoogleLoginCredential(
        @NotEmpty
        String credential) {
}
