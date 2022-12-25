package com.langthang.model.dto.request;

import lombok.Data;

import jakarta.validation.constraints.*;

import java.util.List;

@Data
public class PostRequestDTO {
    @NotNull
    @NotBlank
    @Size(max = 200, message = "Title maximum 200 characters")
    private String title;

    @NotNull
    @NotBlank
    private String content;

    @Size(max = 5, message = "Maximum 5 categories")
    private List<String> categories;

    @NotNull
    @NotBlank(message = "A post should have a thumbnail")
    @Size(max = 250, message = "Link is too long")
    private String postThumbnail;
}