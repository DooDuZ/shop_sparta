package com.sparta.shop_sparta.domain.entity.cart;

import com.sparta.shop_sparta.domain.dto.cart.CartDetailRequestDto;
import com.sparta.shop_sparta.domain.dto.cart.CartDetailResponseDto;
import com.sparta.shop_sparta.domain.dto.product.ProductResponseDto;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import jakarta.persistence.Column;
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

@Entity(name = "cartDetail")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartDetailId;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "productId")
    private ProductEntity productEntity;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "cartId")
    private CartEntity cartEntity;

    @Column(nullable = false)
    private Long amount;


    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public CartDetailRequestDto toRequestDto() {
        return CartDetailRequestDto.builder().productId(this.productEntity.getProductId()).amount(this.amount).build();
    }

    public CartDetailResponseDto toResponseDto() {
        return CartDetailResponseDto.builder().productResponseDto(new ProductResponseDto(this.productEntity)).amount(this.amount).build();
    }
}
