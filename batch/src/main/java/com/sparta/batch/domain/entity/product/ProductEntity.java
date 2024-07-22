package com.sparta.batch.domain.entity.product;

import com.sparta.batch.domain.entity.member.MemberEntity;
import com.sparta.common.constant.product.ProductStatus;
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
import com.sparta.batch.domain.BaseEntity;

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

    public void init(CategoryEntity categoryEntity, MemberEntity sellerEntity) {
        setProductStatus(ProductStatus.NOT_PUBLISHED);
        setCategoryEntity(categoryEntity);
        setSellerEntity(sellerEntity);
    }
}
