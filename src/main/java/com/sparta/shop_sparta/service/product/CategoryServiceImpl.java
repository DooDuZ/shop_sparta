package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import com.sparta.shop_sparta.domain.entity.product.CategoryEntity;
import com.sparta.shop_sparta.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        CategoryEntity categoryEntity = categoryRepository.save(categoryDto.toEntity());
        return categoryEntity.toDto();
    }

    @Override
    public void deleteCategory(CategoryDto categoryDto) {
        categoryRepository.delete(categoryDto.toEntity());
    }
}
