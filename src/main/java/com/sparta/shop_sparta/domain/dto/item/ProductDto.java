package com.sparta.shop_sparta.domain.dto.item;


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
    private String productDetails;
    private Long categoryId;
    private ProductStatus productStatus;
    private Long sellerId;

    @Builder.Default
    private List<productImageDto> productImages = new ArrayList<>();

    public ProductEntity toEntity(){
        return ProductEntity.builder().productId(this.productId).productDetails(this.productDetails)
                .productStatus(this.productStatus).productName(this.productName).build();
    }
}
