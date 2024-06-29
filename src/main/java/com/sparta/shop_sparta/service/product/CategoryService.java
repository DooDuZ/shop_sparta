package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import org.springframework.http.ResponseEntity;

public interface CategoryService {
    ResponseEntity<?> addCategory(CategoryDto categoryDto);
    ResponseEntity<?> deleteCategory(CategoryDto categoryDto);
}
