package com.sparta.shop_sparta.domain.entity.product;

import com.sparta.shop_sparta.domain.dto.product.ProductImageDto;
import com.sparta.shop_sparta.domain.entity.BaseEntity;
import com.sparta.shop_sparta.constant.product.ProductImageType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "productImage")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productImageId;

    @Lob
    @Column(nullable = false)
    private String imagePath;

    @Column(nullable = false)
    private ProductImageType productImageType;

    // 이미지 등록, 수정 등에서 순서를 계속 바꿀 수 있음
    @Column(nullable = false)
    private Byte imageOrdering;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "productId")
    private ProductEntity productEntity;

    public void setProductImageType(ProductImageType productImageType) {
        this.productImageType = productImageType;
    }

    public void setImageOrdering(Byte imageOrdering) {
        this.imageOrdering = imageOrdering;
    }

    public ProductImageDto toDto(){
        return ProductImageDto.builder().productImageId(this.productImageId).productImageType(this.productImageType)
                .itemId(this.productEntity.getProductId()).imageOrdering(this.imageOrdering)
                .imagePath(this.imagePath).build();
    }
}
