package com.moonbaar.domain.category.service;

import com.moonbaar.domain.category.dto.CategoryListResponse;
import com.moonbaar.domain.category.dto.CategoryResponse;
import com.moonbaar.domain.category.exception.CategoryNotFoundException;
import com.moonbaar.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryListResponse getAllCategories() {
        return CategoryListResponse.from(categoryRepository.findAll());
    }

    public CategoryResponse getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .map(CategoryResponse::from)
                .orElseThrow(CategoryNotFoundException::new);
    }
}
