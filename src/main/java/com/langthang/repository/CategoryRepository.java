package com.langthang.repository;

import com.langthang.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    boolean existsByName(String categoryName);

    Optional<Category> findBySlug(String slug);

}