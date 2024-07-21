package com.sparta.shop_sparta.domain.entity.product;

import com.sparta.common.constant.product.ProductStatus;
import com.sparta.shop_sparta.domain.dto.product.ProductRequestDto;
import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.domain.entity.BaseEntity;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
import lombok.Setter;
import lombok.ToString;

@Entity(name = "product")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Setter
public class ProductEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Lob
    @Column(nullable = false)
    private String productDetail;

    @Column(nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "productCategoryId")
    private CategoryEntity categoryEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "memberId")
    private MemberEntity sellerEntity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    @Column(nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long imageVersion;

    public ProductDto toDto() {
        return ProductDto.builder().productId(this.productId).categoryId(this.categoryEntity.getCategoryId())
                .productDetail(this.productDetail).productStatus(this.productStatus).productName(this.productName)
                .sellerId(sellerEntity.getMemberId()).price(this.price).build();
    }

    public void init(CategoryEntity categoryEntity, MemberEntity sellerEntity) {
        setProductStatus(ProductStatus.NOT_PUBLISHED);
        setCategoryEntity(categoryEntity);
        setSellerEntity(sellerEntity);
        setImageVersion(1L);
    }

    public void update(ProductRequestDto productRequestDto) {
        setProductDetail(productRequestDto.getProductDetail());
        setProductName(productRequestDto.getProductName());
        setProductStatus(ProductStatus.of(productRequestDto.getProductStatus()));
        setPrice(productRequestDto.getPrice());
    }

    public void updateVersion() {
        this.imageVersion++;
    }
}
