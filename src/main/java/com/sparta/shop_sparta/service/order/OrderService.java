package com.sparta.shop_sparta.service.order;

import com.sparta.shop_sparta.domain.dto.order.OrderRequestDto;
import com.sparta.shop_sparta.domain.dto.order.OrderResponseDto;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;

public interface OrderService {
    void createOrder(UserDetails userDetails, OrderRequestDto orderRequestDto);
    OrderResponseDto getOrder(UserDetails userDetails, Long orderId);
    OrderResponseDto cancelOrder(UserDetails userDetails, Long orderId);
    OrderResponseDto requestReturn(UserDetails userDetails, Long orderId);
    List<OrderResponseDto> getOrders(UserDetails userDetails, int page, int itemsPerPage);
}
