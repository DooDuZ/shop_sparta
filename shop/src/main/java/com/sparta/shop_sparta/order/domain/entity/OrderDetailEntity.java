package com.sparta.shop_sparta.order.domain.entity;

import com.sparta.shop_sparta.order.domain.dto.OrderDetailDto;
import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
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

@Entity(name="orderDetail")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderDetailId;

    @Column(nullable = false)
    private Long amount;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "productId")
    private ProductEntity productEntity;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "orderId")
    private OrderEntity orderEntity;

    public OrderDetailEntity(ProductEntity productEntity, Long amount, OrderEntity orderEntity) {
        this.productEntity = productEntity;
        this.amount = amount;
        this.orderEntity = orderEntity;
    }

    public OrderDetailDto toDto(){
        return OrderDetailDto.builder().orderDetailId(this.orderDetailId).orderId(this.orderEntity.getOrderId())
                .amount(this.amount).build();
    }
}
