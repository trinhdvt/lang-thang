package com.langthang.services.impl;

import com.langthang.exception.HttpError;
import com.langthang.exception.NotFoundError;
import com.langthang.model.dto.response.CategoryDTO;
import com.langthang.model.entity.Category;
import com.langthang.repository.CategoryRepository;
import com.langthang.services.ICategoryServices;
import com.langthang.utils.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServicesImpl implements ICategoryServices {

    private final CategoryRepository categoryRepo;

    @Autowired
    public CategoryServicesImpl(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Override
    public List<CategoryDTO> getAllCategory(Pageable pageable) {
        return categoryRepo.findAll(pageable)
                .map(CategoryDTO::toCategoryDTO)
                .toList();
    }


    @Override
    public void deleteCategory(int categoryId) {
        categoryRepo.findById(categoryId)
                .ifPresentOrElse(categoryRepo::delete, () -> {
                    throw new NotFoundError(Category.class);
                });
    }

    @Override
    public CategoryDTO modifyCategory(int categoryId, String newName) {
        return categoryRepo.findById(categoryId)
                .map(category -> {
                    if (categoryRepo.existsByName(newName))
                        throw new HttpError("Category is already existed", HttpStatus.CONFLICT);

                    category.setName(newName);
                    return categoryRepo.saveAndFlush(category);
                }).map(CategoryDTO::toCategoryDTO)
                .orElseThrow(() -> new NotFoundError(Category.class));
    }

    @Override
    public CategoryDTO addNewCategory(String categoryName) {
        boolean isCategoryExist = categoryRepo.existsByName(categoryName);

        AssertUtils.isTrue(!isCategoryExist, new HttpError("Category is already existed", HttpStatus.CONFLICT));

        Category newCategory = new Category(categoryName);
        categoryRepo.save(newCategory);

        return CategoryDTO.toCategoryDTO(newCategory);
    }

}