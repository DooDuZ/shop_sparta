package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import com.sparta.shop_sparta.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public ResponseEntity<?> addCategory(CategoryDto categoryDto) {
        categoryRepository.save(categoryDto.toEntity());

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> deleteCategory(CategoryDto categoryDto) {
        categoryRepository.delete(categoryDto.toEntity());
        return ResponseEntity.ok().build();
    }
}
