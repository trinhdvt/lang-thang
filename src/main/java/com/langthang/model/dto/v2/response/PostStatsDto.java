package com.langthang.model.dto.v2.response;

import java.io.Serializable;

public record PostStatsDto(
        Integer bookmarkedCount,
        Integer commentCount
) implements Serializable {
}
