package com.sparta.shop_sparta.controller.order;

import com.sparta.shop_sparta.domain.dto.order.OrderRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface OrderController {
    ResponseEntity<?> createOrder(UserDetails userDetails, OrderRequestDto orderRequestDto);
    ResponseEntity<?> getOrder(UserDetails userDetails, Long orderId);
    ResponseEntity<?> getOrders(UserDetails userDetails, int page, int itemsPerPage);
    ResponseEntity<?> cancelOrder(UserDetails userDetails, Long orderId);
    ResponseEntity<?> requestReturn(UserDetails userDetails, Long orderId);
}
