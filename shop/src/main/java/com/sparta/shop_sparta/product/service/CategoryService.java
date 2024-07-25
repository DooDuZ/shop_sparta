package com.sparta.shop_sparta.product.service;

import com.sparta.shop_sparta.product.domain.dto.CategoryDto;
import com.sparta.shop_sparta.product.domain.entity.CategoryEntity;
import com.sparta.shop_sparta.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService{

    private final CategoryRepository categoryRepository;

    public CategoryDto addCategory(CategoryDto categoryDto) {
        CategoryEntity categoryEntity = categoryRepository.save(categoryDto.toEntity());
        return categoryEntity.toDto();
    }

    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
