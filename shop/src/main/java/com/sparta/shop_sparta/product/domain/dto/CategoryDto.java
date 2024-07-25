package com.sparta.shop_sparta.product.domain.dto;

import com.sparta.shop_sparta.product.domain.entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Long categoryId;
    private String categoryName;

    public CategoryEntity toEntity(){
        return CategoryEntity.builder().categoryId(this.categoryId).categoryName(this.categoryName).build();
    }
}
