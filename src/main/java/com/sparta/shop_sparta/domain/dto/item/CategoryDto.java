package com.sparta.shop_sparta.domain.dto.item;

import com.sparta.shop_sparta.domain.entity.product.categoryEntity;
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

    public categoryEntity toEntity(){
        return categoryEntity.builder().categoryId(this.categoryId).categoryName(this.categoryName).build();
    }
}
