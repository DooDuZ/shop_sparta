package com.sparta.shop_sparta.domain.dto.product;

import com.sparta.shop_sparta.domain.entity.product.CategoryEntity;
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
