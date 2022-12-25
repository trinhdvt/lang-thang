package com.langthang.model.dto.v2.response;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

public record PostDtoV2(
        long id,
        String title,
        String slug,
        String content,
        Instant publishedDate,
        Instant createdDate,
        String postThumbnail,
        boolean isPublished,
        UserDtoV2 author,
        PostStatsDto stats,
        Set<CategoryDtoV2> categories
) implements Serializable {
}
