package com.sparta.shop_sparta.order.domain.dto;

import com.sparta.shop_sparta.order.domain.entity.OrderEntity;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class OrderRequestDto {
    private Long orderId;
    private String orderAddr;
    private String orderAddrDetail;
    private Long memberId;
    private Long orderStatus;
    private List<OrderDetailRequestDto> orderDetails;

    public OrderEntity toEntity(){
        return OrderEntity.builder().orderId(this.orderId).orderAddr(this.orderAddr).orderAddrDetail(this.orderAddrDetail).build();
    }
}
