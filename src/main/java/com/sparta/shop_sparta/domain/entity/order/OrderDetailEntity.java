package com.sparta.shop_sparta.domain.entity.order;

import com.sparta.shop_sparta.domain.dto.order.OrderDetailDto;
import com.sparta.shop_sparta.domain.entity.BaseEntity;
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

@Entity(name="orderDetail")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
                .productId(this.productEntity.getProductId()).amount(this.amount).build();
    }
}
