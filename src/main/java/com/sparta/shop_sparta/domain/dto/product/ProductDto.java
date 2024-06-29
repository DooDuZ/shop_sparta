package com.sparta.shop_sparta.domain.dto.product;


import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.constant.product.ProductStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long productId;
    private String productName;
    private String productDetail;
    private Long categoryId;
    private ProductStatus productStatus;
    private Long sellerId;

    @Builder.Default
    private List<ProductImageDto> productImages = new ArrayList<>();

    public ProductEntity toEntity(){
        return ProductEntity.builder().productId(this.productId).productDetail(this.productDetail)
                .productStatus(this.productStatus).productName(this.productName).build();
    }
}
