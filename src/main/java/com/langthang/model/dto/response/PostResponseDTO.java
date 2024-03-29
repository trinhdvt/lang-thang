package com.langthang.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.langthang.model.entity.Post;
import com.langthang.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponseDTO implements Serializable {

    private int postId;

    private String title;

    private AccountDTO author;

    private String content;

    private String slug;

    private boolean isOwner;

    private boolean isBookmarked;

    private Instant publishedDate;

    private Instant createdDate;

    private String postThumbnail;

    private int bookmarkedCount;

    private int commentCount;

    private Set<CategoryDTO> categories;

    public PostResponseDTO(int postId, String slug) {
        this.postId = postId;
        this.slug = slug;
    }

    public static PostResponseDTO toPostResponseDTO(Post entity) {
        if (entity == null)
            return null;

        return PostResponseDTO.builder()
                .postId(entity.getId())
                .title(entity.getTitle())
                .slug(entity.getSlug())
                .content(entity.getContent())
                .postThumbnail(entity.getPostThumbnail())
                .createdDate(entity.getCreatedDate())
                .publishedDate(entity.getPublishedDate())
                .isBookmarked(entity.getBookmarkedPosts().stream()
                        .anyMatch(bp -> bp.getAccount().getEmail().equals(SecurityUtils.getLoggedInEmail())))
                .bookmarkedCount(entity.getBookmarkedPosts().size())
                .commentCount(entity.getComments().size())
                .categories(entity.getCategories().stream().map(CategoryDTO::toCategoryDTO).collect(Collectors.toSet()))
                .build();
    }
}