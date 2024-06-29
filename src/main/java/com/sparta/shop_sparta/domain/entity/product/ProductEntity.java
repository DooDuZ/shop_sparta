package com.sparta.shop_sparta.domain.entity.product;

import com.sparta.shop_sparta.domain.dto.item.ProductDto;
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
public class ProductEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Lob
    @Column(nullable = false)
    private String productDetails;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "productCategoryId")
    private categoryEntity categoryEntity;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "memberId")
    private MemberEntity sellerEntity;

    @Column(nullable = false)
    private ProductStatus productStatus;



    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductDetails(String productDetails) {
        this.productDetails = productDetails;
    }

    public void setCategoryEntity(categoryEntity categoryEntity) {
        this.categoryEntity = categoryEntity;
    }

    public void setProductStatus(ProductStatus productStatus) {
        this.productStatus = productStatus;
    }

    public ProductDto toDto(){
        return ProductDto.builder().productId(this.productId).categoryId(this.categoryEntity.getCategoryId()).productDetails(this.productDetails)
                .productStatus(this.productStatus).productName(this.productName).build();
    }
}
