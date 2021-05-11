package com.langthang.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String title;

    @NotNull
    private String content;

    @NotNull
    private String postThumbnail;
}
