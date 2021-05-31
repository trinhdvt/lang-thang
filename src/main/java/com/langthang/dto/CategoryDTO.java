package com.langthang.dto;

import com.langthang.model.Category;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CategoryDTO {
    int categoryId;
    String categoryName;
    int postCount;

    public static CategoryDTO toCategoryDTO(Category entity) {
        return CategoryDTO.builder()
                .categoryId(entity.getId())
                .categoryName(entity.getName())
                .postCount(entity.getPostCategories().size())
                .build();
    }
}
