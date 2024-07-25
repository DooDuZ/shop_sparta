package com.sparta.shop_sparta.product.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sparta.shop_sparta.product.domain.dto.CategoryDto;
import com.sparta.shop_sparta.product.domain.entity.CategoryEntity;
import com.sparta.shop_sparta.product.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @InjectMocks
    CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void createCategorySuccessTest() {
        // given
        CategoryDto categoryDto = CategoryDto.builder().categoryId(1L).categoryName("콤퓨타").build();
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(categoryDto.toEntity());

        // when
        CategoryDto category = categoryService.addCategory(categoryDto);

        // then
        assertThat(category.getCategoryId()).isEqualTo(categoryDto.getCategoryId());
    }

    @Test
    void deleteCategorySuccessTest() {
        // given
        CategoryDto categoryDto = CategoryDto.builder().categoryId(1L).categoryName("콤퓨타").build();
        //doNothing().when(categoryRepository).delete(any(CategoryEntity.class));

        // when
        categoryService.deleteCategory(categoryDto.getCategoryId());

        // then
        verify(categoryRepository).deleteById(any(Long.class));
    }
}
