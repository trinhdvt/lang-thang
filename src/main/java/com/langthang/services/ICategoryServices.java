package com.langthang.services;

import com.langthang.dto.CategoryDTO;

import java.util.List;

public interface ICategoryServices {
    List<CategoryDTO> getAllCategory();

    void deleteCategory(int categoryId);

    CategoryDTO modifyCategory(int categoryId, String newName);

    CategoryDTO addNewCategory(String categoryName);
}
