package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import org.springframework.http.ResponseEntity;

public interface CategoryService {
    CategoryDto addCategory(CategoryDto categoryDto);
    void deleteCategory(CategoryDto categoryDto);
}
