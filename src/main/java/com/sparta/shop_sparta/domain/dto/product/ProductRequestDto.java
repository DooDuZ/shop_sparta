package com.sparta.shop_sparta.domain.dto.product;

import com.sparta.shop_sparta.constant.product.ProductStatus;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
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
    private ProductStatus productStatus;
    private Long sellerId;
    private List<MultipartFile> productThumbnails;
    private List<MultipartFile> productDetailImages;

    public ProductEntity toEntity(){
        return ProductEntity.builder().productId(this.productId).productDetail(this.productDetail)
                .productStatus(this.productStatus).productName(this.productName).build();
    }
}
