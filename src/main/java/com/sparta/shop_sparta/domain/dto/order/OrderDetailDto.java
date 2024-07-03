package com.sparta.shop_sparta.domain.dto.order;

import com.sparta.shop_sparta.domain.dto.product.ProductResponseDto;
import com.sparta.shop_sparta.domain.entity.order.OrderDetailEntity;
import java.util.List;
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
    private Long amount;
    private Long orderId;
    private ProductResponseDto productResponseDto;

    OrderDetailEntity toEntity(){
        return OrderDetailEntity.builder().orderDetailId(this.orderDetailId).amount(this.amount).build();
    }
}
