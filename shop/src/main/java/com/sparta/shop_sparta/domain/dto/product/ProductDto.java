package com.sparta.shop_sparta.domain.dto.product;

import com.sparta.common.constant.product.ProductStatus;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import java.io.Serializable;
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
@Setter
public class ProductDto implements Serializable {
    private Long productId;
    private String productName;
    private String productDetail;
    private Long categoryId;

    private Long price;
    private Long amount;

    private ProductStatus productStatus;
    private Long sellerId;

    @Builder.Default
    private List<ProductImageDto> productImages = new ArrayList<>();

    @Builder.Default
    private List<ReservationResponseDto> reservationResponseDtoList = new ArrayList<>();

    public ProductDto(ProductEntity productEntity) {
        this.productId = productEntity.getProductId();
        this.productName = productEntity.getProductName();
        this.productDetail = productEntity.getProductDetail();
        this.categoryId = productEntity.getCategoryEntity().getCategoryId();
        this.price = productEntity.getPrice();
        this.productStatus = productEntity.getProductStatus();
        this.sellerId = productEntity.getSellerEntity().getMemberId();
    }

    public ProductEntity toEntity(){
        return ProductEntity.builder()
                .productId(this.productId)
                .productName(this.productName)
                .price(this.price)
                .productStatus(this.productStatus)
                .build();
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
