package com.sparta.shop_sparta.domain.dto.product;


import com.sparta.common.constant.product.ProductImageType;
import com.sparta.shop_sparta.domain.entity.product.ProductImageEntity;
import java.io.Serializable;
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
public class ProductImageDto implements Serializable {
    private Long productImageId;
    private String imageData;
    private Byte imageOrdering;
    private Long productId;
    private ProductImageType productImageType;

    public ProductImageEntity toEntity(){
        return ProductImageEntity.builder().productImageId(this.productImageId).imagePath(this.imageData).imageOrdering(this.imageOrdering)
                .productImageType(this.productImageType).build();
    }
}
