package com.sparta.shop_sparta.controller.order;

import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.domain.dto.order.OrderDto;
import java.util.List;

public interface OrderController {
    OrderDto addOrder(OrderDto orderDTO);
    List<OrderDto> getOrders(MemberDto memberDTO);
    OrderDto cancelOrder(OrderDto orderDTO);
}
