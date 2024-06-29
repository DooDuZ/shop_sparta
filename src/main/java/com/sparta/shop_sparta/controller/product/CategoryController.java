package com.sparta.shop_sparta.controller.product;

import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import org.springframework.http.ResponseEntity;

public interface CategoryController {
    ResponseEntity<?> addCategory(CategoryDto categoryDto);
    ResponseEntity<?> deleteCategory(CategoryDto categoryDto);
}
