package com.sparta.shop_sparta.domain.entity.product;

import com.sparta.shop_sparta.domain.dto.item.CategoryDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "category")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class categoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = false)
    private String categoryName;

    public void setItemTypeName(String categoryName) {
        this.categoryName = categoryName;
    }

    public CategoryDto toDto(){
        return CategoryDto.builder().categoryId(this.categoryId).categoryName(this.categoryName).build();
    }
}
