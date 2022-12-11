package com.langthang.model.dto.response.v2;

public record PostDtoV2(
        long id,
        String title,
        String slug,
        String content,
        String thumbnail,
        String publishedDate,
        String postThumbnail,
        String author,
        String authorEmail,
        String authorAvatar
) {
}
