package com.langthang.model.dto.response;

import com.langthang.model.entity.Category;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CategoryDTO {

    private int categoryId;

    private String categoryName;

    private int postCount;

    public static CategoryDTO toCategoryDTO(Category entity) {
        return CategoryDTO.builder()
                .categoryId(entity.getId())
                .categoryName(entity.getName())
                .postCount(entity.getPostCategories().size())
                .build();
    }
}