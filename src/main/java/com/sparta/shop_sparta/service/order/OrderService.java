package com.sparta.shop_sparta.service.order;

import com.sparta.shop_sparta.domain.dto.order.OrderRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface OrderService {
    ResponseEntity<?> createOrder(UserDetails userDetails, OrderRequestDto orderRequestDto);
    ResponseEntity<?> getOrders(UserDetails userDetails, Long orderId);
    ResponseEntity<?> cancelOrder(UserDetails userDetails, Long orderId);
}
