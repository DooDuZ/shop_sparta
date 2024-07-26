package com.sparta.shop_sparta.order.domain.dto;

import com.sparta.common.constant.order.OrderStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {
    private Long orderId;
    private String orderAddr;
    private String orderAddrDetail;
    private Long memberId;
    private OrderStatus orderStatus;
    private Long totalPrice;

    @Builder.Default
    private List<OrderDetailDto> orderDetails = new ArrayList<>();

    public void setOrderDetails(List<OrderDetailDto> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
