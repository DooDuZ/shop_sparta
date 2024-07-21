package com.sparta.batch.domain.entity.product;

import com.sparta.batch.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import com.sparta.batch.constant.ProductImageType;

@Entity(name = "productImage")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class ProductImageEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productImageId;

    @Lob
    @Column(nullable = false)
    private String imagePath;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductImageType productImageType;

    // 이미지 등록, 수정 등에서 순서를 계속 바꿀 수 있음
    @Column(nullable = false)
    private Byte imageOrdering;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "productId")
    private ProductEntity productEntity;

    @Column(nullable = false)
    private Long imageVersion;
}
