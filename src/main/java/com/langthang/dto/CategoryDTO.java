package com.langthang.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CategoryDTO {
    int categoryId;
    String categoryName;
    int postCount;
}
