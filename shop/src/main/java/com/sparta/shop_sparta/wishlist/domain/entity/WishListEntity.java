package com.sparta.shop_sparta.wishlist.domain.entity;

import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import com.sparta.shop_sparta.member.domain.entity.MemberEntity;
import com.sparta.shop_sparta.wishlist.domain.dto.WishListResponseDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "wishList")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishListEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishListId;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "memberId")
    private MemberEntity memberEntity;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "productId")
    private ProductEntity productEntity;

    public WishListEntity(MemberEntity memberEntity, ProductEntity productEntity) {
        this.memberEntity = memberEntity;
        this.productEntity = productEntity;
    }

    public WishListResponseDto toDto(){
        return WishListResponseDto.builder()
                .wishlistId(this.wishListId)
                .memberId(this.memberEntity.getMemberId())
                .productId(this.productEntity.getProductId())
                .build();
    }
}
