package com.sparta.shop_sparta.order.domain.dto;

import com.sparta.shop_sparta.product.domain.dto.ProductDto;
import com.sparta.shop_sparta.order.domain.entity.OrderDetailEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Setter
public class OrderDetailDto {
    private Long orderDetailId;
    private Long amount;
    private Long orderId;
    private ProductDto productDto;

    OrderDetailEntity toEntity(){
        return OrderDetailEntity.builder().orderDetailId(this.orderDetailId).amount(this.amount).build();
    }
}
