package com.sparta.shop_sparta.order.domain.entity;

import com.sparta.common.constant.order.OrderStatus;
import com.sparta.shop_sparta.order.domain.dto.OrderResponseDto;
import com.sparta.shop_sparta.common.domain.BaseEntity;
import com.sparta.shop_sparta.member.domain.entity.MemberEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity(name = "orders")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private String orderAddr;

    @Column
    private String orderAddrDetail;

    @Column
    private Long totalPrice;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "memberId")
    private MemberEntity memberEntity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public void setMemberEntity(MemberEntity memberEntity) {
        this.memberEntity = memberEntity;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderResponseDto toDto(){
        return OrderResponseDto.builder().orderId(this.orderId).orderAddr(this.orderAddr).orderAddrDetail(this.orderAddrDetail)
                .memberId(this.memberEntity.getMemberId()).orderStatus(this.orderStatus).totalPrice(this.totalPrice).build();
    }
}
