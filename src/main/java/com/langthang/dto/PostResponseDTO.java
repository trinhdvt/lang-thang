package com.langthang.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.langthang.model.Post;
import com.langthang.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

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

    public static PostResponseDTO toPostResponseDTO(Post entity){
        return PostResponseDTO.builder()
                .postId(entity.getId())
                .title(entity.getTitle())
                .slug(entity.getSlug())
                .content(entity.getContent())
                .postThumbnail(entity.getPostThumbnail())
                .publishedDate(entity.getPublishedDate())
                .isBookmarked(entity.getBookmarkedPosts().stream()
                        .anyMatch(bp -> bp.getAccount().getEmail().equals(Utils.getCurrentAccEmail())))
                .bookmarkedCount(entity.getBookmarkedPosts().size())
                .commentCount(entity.getComments().size())
                .categories(entity.getPostCategories().stream().map(CategoryDTO::toCategoryDTO).collect(Collectors.toSet()))
                .build();
    }
}
