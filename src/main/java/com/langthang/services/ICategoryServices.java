package com.langthang.services;

import com.langthang.dto.CategoryDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICategoryServices {
    List<CategoryDTO> getAllCategory(Pageable pageable);

    void deleteCategory(int categoryId);

    CategoryDTO modifyCategory(int categoryId, String newName);

    CategoryDTO addNewCategory(String categoryName);
}
