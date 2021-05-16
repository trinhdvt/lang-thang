package com.langthang.controller;

import com.langthang.dto.CategoryDTO;
import com.langthang.dto.PostResponseDTO;
import com.langthang.services.ICategoryServices;
import com.langthang.services.IPostServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private ICategoryServices categoryServices;

    @Autowired
    private IPostServices postServices;

    @GetMapping("/category")
    public ResponseEntity<Object> getListCategory() {

        List<CategoryDTO> categoryList = categoryServices.getAllCategory();

        return ResponseEntity.ok(categoryList);
    }

    @GetMapping("/category/{category_id}/post")
    public ResponseEntity<Object> getAllPostOfCategory(
            @PathVariable("category_id") int categoryId,
            @PageableDefault Pageable pageable) {

        List<PostResponseDTO> postOfCategories = postServices.getAllPreviewPostOfCategory(categoryId, pageable);

        return ResponseEntity.ok(postOfCategories);
    }

    @PostMapping("/category")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> addNewCategory(
            @RequestParam("name") @NotBlank String categoryName) {

        CategoryDTO newCategory = categoryServices.addNewCategory(categoryName);

        return ResponseEntity.ok(newCategory);
    }

    @DeleteMapping("/category/{category_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> deleteCategory(
            @PathVariable("category_id") int categoryId) {

        categoryServices.deleteCategory(categoryId);

        return ResponseEntity.noContent().build();
    }


    @PutMapping("/category/{category_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> modifyCategory(
            @PathVariable("category_id") int categoryId,
            @RequestParam("name") @NotBlank String newName) {

        categoryServices.modifyCategory(categoryId, newName);

        return ResponseEntity.accepted().build();
    }
}
