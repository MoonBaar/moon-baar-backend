package com.moonbaar.domain.category.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.moonbaar.common.config.SecurityTestConfig;
import com.moonbaar.common.exception.BusinessException;
import com.moonbaar.domain.category.dto.CategoryListResponse;
import com.moonbaar.domain.category.dto.CategoryResponse;
import com.moonbaar.domain.category.exception.CategoryErrorCode;
import com.moonbaar.domain.category.service.CategoryService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
@Import(SecurityTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void getCategories_shouldReturnCategoryList() throws Exception {
        // given
        CategoryResponse category1 = new CategoryResponse(1L, "연극");
        CategoryResponse category2 = new CategoryResponse(2L, "전시/미술");
        CategoryListResponse responseDTO = new CategoryListResponse(List.of(category1, category2));

        when(categoryService.getAllCategories()).thenReturn(responseDTO);

        // when & then
        mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getCategoryByExactName_shouldReturnSingleCategory() throws Exception {
        // given
        CategoryResponse category = new CategoryResponse(1L, "연극");

        when(categoryService.getCategoryByName("연극")).thenReturn(category);

        // when & then
        mockMvc.perform(get("/categories/by-name/연극")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getCategoryByExactName_shouldReturn404WhenNotFound() throws Exception {
        // given
        when(categoryService.getCategoryByName(anyString()))
                .thenThrow(new BusinessException(CategoryErrorCode.CATEGORY_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/categories/by-name/존재하지않는카테고리")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
