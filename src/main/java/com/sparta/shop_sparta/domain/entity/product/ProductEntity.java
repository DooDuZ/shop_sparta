package com.sparta.shop_sparta.domain.entity.product;

import com.sparta.shop_sparta.domain.dto.product.ProductResponseDto;
import com.sparta.shop_sparta.domain.entity.BaseEntity;
import com.sparta.shop_sparta.constant.product.ProductStatus;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
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

@Entity(name = "product")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Lob
    @Column(nullable = false)
    private String productDetail;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "productCategoryId")
    private CategoryEntity categoryEntity;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "memberId")
    private MemberEntity sellerEntity;

    @Column(nullable = false)
    private ProductStatus productStatus;


    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductDetail(String productDetail) {
        this.productDetail = productDetail;
    }

    public void setCategoryEntity(CategoryEntity categoryEntity) {
        this.categoryEntity = categoryEntity;
    }

    public void setProductStatus(ProductStatus productStatus) {
        this.productStatus = productStatus;
    }

    public void setSellerEntity(MemberEntity sellerEntity) {
        this.sellerEntity = sellerEntity;
    }

    public ProductResponseDto toDto(){
        return ProductResponseDto.builder().productId(this.productId).categoryId(this.categoryEntity.getCategoryId()).productDetail(this.productDetail)
                .productStatus(this.productStatus).productName(this.productName).sellerId(sellerEntity.getMemberId()).build();
    }
}
