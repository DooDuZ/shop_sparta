package com.sparta.shop_sparta.domain.entity.order;

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

@Entity(name="orderDetail")
@NoArgsConstructor
@Getter
public class OrderDetailEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderDetailId;

    // unsigned 쓰는게 가장 안전하지만 java엔 없고
    // 이미 결제된 상태에서 order에 넘어온다. 수정 시 결제 관련 이슈가 많이 생길듯
    // 취소 후 다시 결제하는 게 맞다.
    // 취소 후 같은 상품 그대로 다시 담기 기능이 있으면 좋을 것 같음
    // 이런 주석을 여기다 다는 게 맞을까? 나중에 노션으로 옮기자
    @Column(nullable = false)
    private Integer amount;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "itemId")
    private ItemEntity itemEntity;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "orderId")
    private OrderEntity orderEntity;

    public OrderDetailEntity(ItemEntity itemEntity, Integer amount, OrderEntity orderEntity) {
        this.itemEntity = itemEntity;
        this.amount = amount;
        this.orderEntity = orderEntity;
    }
}
