package com.langthang.controller;

import com.langthang.model.dto.response.CategoryDTO;
import com.langthang.model.dto.response.PostResponseDTO;
import com.langthang.services.ICategoryServices;
import com.langthang.services.IPostServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@CacheConfig(cacheNames = "categoryCache")
public class CategoryController {

    private final ICategoryServices categoryServices;

    private final IPostServices postServices;

    @GetMapping("/category")
    @Cacheable(key = "{#root.methodName,#pageable}")
    public ResponseEntity<Object> getListOfCategory(
            @PageableDefault(sort = {"name"},
                    size = Integer.MAX_VALUE) Pageable pageable) {

        List<CategoryDTO> categoryList = categoryServices.getAllCategory(pageable);

        return ResponseEntity.ok(categoryList);
    }

    @GetMapping("/category/{category_id}/post")
    public ResponseEntity<Object> getAllPostOfCategory(
            @PathVariable("category_id") int categoryId,
            @PageableDefault Pageable pageable) {

        List<PostResponseDTO> postOfCategories = postServices.getAllPostOfCategory(categoryId, pageable);

        return ResponseEntity.ok(postOfCategories);
    }

    @PostMapping("/category")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @CacheEvict(allEntries = true)
    public ResponseEntity<Object> addNewCategory(
            @RequestParam("name") @NotBlank
            @Size(max = 250) String categoryName) {

        CategoryDTO newCategory = categoryServices.addNewCategory(categoryName);

        return ResponseEntity.ok(newCategory);
    }

    @DeleteMapping("/category/{category_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @CacheEvict(allEntries = true)
    public ResponseEntity<Object> deleteCategory(
            @PathVariable("category_id") int categoryId) {

        categoryServices.deleteCategory(categoryId);

        return ResponseEntity.noContent().build();
    }


    @PutMapping("/category/{category_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @CacheEvict(allEntries = true)
    public ResponseEntity<Object> modifyCategoryName(
            @PathVariable("category_id") int categoryId,
            @RequestParam("name") @Size(max = 250)
            @NotBlank String newName) {

        categoryServices.modifyCategory(categoryId, newName);

        return ResponseEntity.accepted().build();
    }
}