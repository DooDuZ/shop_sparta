package com.sparta.batch.domain.entity.order;

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
import com.sparta.batch.domain.entity.product.ProductEntity;

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
}
