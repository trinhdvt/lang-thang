package com.langthang.model.dto.v2.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostCreateDto(
        @NotBlank(message = "Title can't be empty")
        @Size(max = 200, message = "Title maximum 200 characters")
        String title,

        @NotBlank(message = "Content can't be empty")
        String content,

        @Size(max = 250, message = "Link is too long")
        String postThumbnail
) {
}
