package com.sparta.shop_sparta.domain.dto.product;

import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.constant.product.ProductStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {
    private Long productId;
    private String productName;
    private String productDetail;
    private Long categoryId;

    private Long price;
    private Long amount;

    private ProductStatus productStatus;
    private Long sellerId;

    @Setter
    @Builder.Default
    private List<ProductImageDto> productImages = new ArrayList<>();
}
