package com.langthang.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponseDTO {
    private int postId;

    private String title;

    private AccountDTO author;

    private String content;

    private String slug;

    private boolean isOwner = false;

    private boolean isBookmarked = false;

    private Date publishedDate;

    private String postThumbnail;

    private int bookmarkedCount;

    private int commentCount;

    private Set<CategoryDTO> categories;

    public PostResponseDTO(int postId, String title, String slug, Date publishedDate, String postThumbnail) {
        this.postId = postId;
        this.title = title;
        this.publishedDate = publishedDate;
        this.postThumbnail = postThumbnail;
        this.slug = slug;
    }

}
