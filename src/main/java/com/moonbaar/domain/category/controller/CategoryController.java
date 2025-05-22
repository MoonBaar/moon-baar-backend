package com.moonbaar.domain.category.controller;

import com.moonbaar.domain.category.dto.CategoryListResponse;
import com.moonbaar.domain.category.dto.CategoryResponse;
import com.moonbaar.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public CategoryListResponse getCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/by-name/{name}")
    public CategoryResponse getCategoryByName(@PathVariable String name) {
        return categoryService.getCategoryByName(name);
    }
}
