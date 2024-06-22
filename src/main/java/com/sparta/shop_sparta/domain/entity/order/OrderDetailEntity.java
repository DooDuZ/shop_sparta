package com.sparta.shop_sparta.domain.entity.order;

import com.sparta.shop_sparta.domain.dto.order.OrderDetailDto;
import com.sparta.shop_sparta.domain.entity.BaseEntity;
import com.sparta.shop_sparta.domain.entity.item.ItemEntity;
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
public class OrderDetailEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderDetailId;

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

    public OrderDetailDto toDto(){
        return OrderDetailDto.builder().orderDetailId(this.orderDetailId).orderId(this.orderEntity.getOrderId())
                .itemId(this.itemEntity.getItemId()).amount(this.amount).build();
    }
}
