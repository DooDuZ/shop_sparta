package com.sparta.shop_sparta.service.order;

import com.sparta.shop_sparta.domain.dto.order.OrderDto;
import java.util.List;

public interface OrderService {
    OrderDto addOrder(OrderDto orderDTO);
    List<OrderDto> getOrders(Long memberId);
    OrderDto cancelOrder(OrderDto orderDTO);
}
