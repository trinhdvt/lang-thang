package com.langthang.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PostRequestDTO {
    /**
     * An identity ID of this post
     */
    private Integer postId;

    @NotNull
    @NotBlank
    private String title;

    @NotNull
    @NotBlank
    private String content;

    @NotNull
    @NotBlank(message = "A post should have a thumbnail")
    private String postThumbnail;
}
