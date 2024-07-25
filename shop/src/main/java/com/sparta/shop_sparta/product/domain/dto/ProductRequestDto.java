package com.sparta.shop_sparta.product.domain.dto;

import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Setter
public class ProductRequestDto {
    private Long productId;
    private String productName;
    private String productDetail;
    private Long categoryId;
    private Long sellerId;

    private Long price;
    private Long amount;

    private Long productStatus;
    private List<MultipartFile> productThumbnails;
    private List<MultipartFile> productDetailImages;

    private boolean reservation;
    private LocalDateTime reservationTime;
    private Long reservationStatus;

    public ProductEntity toEntity(){
        return ProductEntity.builder().productId(this.productId).productDetail(this.productDetail)
                .productName(this.productName).price(this.price).build();
    }
}
