package com.sparta.shop_sparta.domain.dto.product;


import com.sparta.shop_sparta.domain.entity.product.ProductImageEntity;
import com.sparta.shop_sparta.constant.product.ProductImageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class ProductImageDto {
    private Long productImageId;
    private String imagePath;
    private Byte imageOrdering;
    private Long productId;
    private ProductImageType productImageType;
    private String imageData;

    public ProductImageEntity toEntity(){
        return ProductImageEntity.builder().productImageId(this.productImageId).imagePath(this.imagePath).imageOrdering(this.imageOrdering)
                .productImageType(this.productImageType).build();
    }
}
