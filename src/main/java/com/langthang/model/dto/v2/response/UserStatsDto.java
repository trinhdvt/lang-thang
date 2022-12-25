package com.langthang.model.dto.v2.response;

public record UserStatsDto(
        long postCount,
        long followCount,
        long receivedBookmarkCount,
        long receivedCommentCount
) {
}
