package com.langthang.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class PostRequestDTO {
    @NotNull
    @NotBlank
    @Size(max = 200, message = "Title maximum 200 characters")
    private String title;

    @NotNull
    @NotBlank
    private String content;

    @Size(max = 5, message = "Maximum 5 categories")
    private String[] categories;

    @NotNull
    @NotBlank(message = "A post should have a thumbnail")
    @Size(max = 250, message = "Link is too long")
    private String postThumbnail;
}
