package com.langthang.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagDTO {
    private int tagId;
    private String tagName;
    private int tagCount;
}
