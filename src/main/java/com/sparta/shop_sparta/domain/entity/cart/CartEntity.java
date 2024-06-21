package com.sparta.shop_sparta.domain.entity.cart;

import com.sparta.shop_sparta.domain.dto.cart.CartDTO;
import com.sparta.shop_sparta.domain.entity.BaseEntity;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.domain.entity.constant.CartStatus;
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

@Entity(name = "cart")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @Column(nullable = false)
    private CartStatus cartStatus;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "memberId")
    private MemberEntity memberEntity;

    public CartEntity(CartStatus cartStatus, MemberEntity memberEntity) {
        this.cartStatus = cartStatus;
        this.memberEntity = memberEntity;
    }

    public void setCartStatus(CartStatus cartStatus) {
        this.cartStatus = cartStatus;
    }

    public CartDTO toDTO() {
        return CartDTO.builder().cartId(this.cartId).cartStatus(this.cartStatus)
                .memberId(this.memberEntity.getMemberId()).build();
    }
}
