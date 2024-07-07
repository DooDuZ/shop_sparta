package com.sparta.shop_sparta.controller.product;

import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import org.springframework.http.ResponseEntity;

public interface CategoryController {
    ResponseEntity<CategoryDto> createCategory(CategoryDto categoryDto);
    ResponseEntity<Void> deleteCategory(CategoryDto categoryDto);
}
