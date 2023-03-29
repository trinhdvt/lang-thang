package com.langthang.model.dto.v2.request;

import java.util.Set;

public record PostStatsRequest(
        Set<Integer> postIds
) {
}
