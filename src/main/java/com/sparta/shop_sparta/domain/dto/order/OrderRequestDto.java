package com.sparta.shop_sparta.domain.dto.order;

import com.sparta.shop_sparta.constant.order.OrderStatus;
import com.sparta.shop_sparta.domain.entity.order.OrderEntity;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderRequestDto {
    private Long orderId;
    private String orderAddr;
    private String orderAddrDetail;
    private Long memberId;
    private Long orderStatus;
    private List<OrderDetailDto> orderDetails;

    public OrderEntity toEntity(){
        return OrderEntity.builder().orderAddr(this.orderAddr).orderAddrDetail(this.orderAddrDetail)
                .orderStatus(OrderStatus.of(this.orderStatus)).build();
    }
}
