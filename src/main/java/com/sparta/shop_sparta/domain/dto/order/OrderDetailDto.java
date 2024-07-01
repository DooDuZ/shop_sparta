package com.sparta.shop_sparta.domain.dto.order;

import com.sparta.shop_sparta.domain.entity.order.OrderDetailEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDto {
    private Long orderDetailId;
    private Long productId;
    private Long amount;
    private Long orderId;

    OrderDetailEntity toEntity(){
        return OrderDetailEntity.builder().orderDetailId(this.orderDetailId).amount(this.amount).build();
    }
}
