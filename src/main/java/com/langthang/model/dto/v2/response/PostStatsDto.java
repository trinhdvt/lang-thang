package com.langthang.model.dto.v2.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostStatsDto implements Serializable {
    private Integer bookmarkedCount;
    private Integer commentCount;
    private boolean isBookmarked;
}
