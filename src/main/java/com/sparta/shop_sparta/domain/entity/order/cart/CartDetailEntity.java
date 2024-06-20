package com.sparta.shop_sparta.domain.entity.order.cart;

import com.sparta.shop_sparta.domain.entity.BaseEntity;
import com.sparta.shop_sparta.domain.entity.item.ItemEntity;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "cartDetail")
@NoArgsConstructor
@Getter
public class CartDetailEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartDetailId;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "itemId")
    private ItemEntity itemEntity;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "cartId")
    private CartEntity cartEntity;

    @Column(nullable = false)
    private Integer amount;

    public CartDetailEntity(ItemEntity itemEntity, CartEntity cartEntity, Integer amount) {
        this.itemEntity = itemEntity;
        this.cartEntity = cartEntity;
        this.amount = amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
