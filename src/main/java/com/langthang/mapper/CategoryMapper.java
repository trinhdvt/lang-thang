package com.langthang.mapper;

import com.langthang.model.dto.v2.response.CategoryDtoV2;
import com.langthang.model.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDtoV2 toDto(Category source);
}
