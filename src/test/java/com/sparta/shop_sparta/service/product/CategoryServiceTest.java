package com.sparta.shop_sparta.service.product;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import com.sparta.shop_sparta.domain.entity.product.CategoryEntity;
import com.sparta.shop_sparta.repository.CategoryRepository;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @InjectMocks
    CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void createCategorySuccessTest() {
        // given
        CategoryDto categoryDto = CategoryDto.builder().categoryId(1L).categoryName("콤퓨타").build();
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(categoryDto.toEntity());

        // when
        ResponseEntity<?> response = categoryService.addCategory(categoryDto);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void deleteCategorySuccessTest() {
        // given
        CategoryDto categoryDto = CategoryDto.builder().categoryId(1L).categoryName("콤퓨타").build();
        doNothing().when(categoryRepository).delete(any(CategoryEntity.class));

        // when
        ResponseEntity<?> response = categoryService.deleteCategory(categoryDto);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
