package com.langthang.model.dto.response;

import com.langthang.model.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CategoryDTO {

    private int categoryId;

    private String categoryName;

    private String slug;

    private int postCount;

    public static CategoryDTO toCategoryDTO(Category entity) {
        return CategoryDTO.builder()
                .categoryId(entity.getId())
                .categoryName(entity.getName())
                .slug(entity.getSlug())
                .postCount(entity.getPostCategories().size())
                .build();
    }
}