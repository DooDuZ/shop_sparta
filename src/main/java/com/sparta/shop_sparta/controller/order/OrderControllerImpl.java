package com.sparta.shop_sparta.controller.order;

import com.sparta.shop_sparta.domain.dto.order.OrderRequestDto;
import com.sparta.shop_sparta.domain.dto.order.OrderResponseDto;
import com.sparta.shop_sparta.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderControllerImpl implements OrderController{

    private final OrderService orderService;

    @Override
    @PostMapping
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal UserDetails userDetails, @RequestBody OrderRequestDto orderRequestDto) {
        orderService.createOrder(userDetails, orderRequestDto);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrders(userDetails, orderId));
    }

    @Override
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<OrderResponseDto> cancelOrder(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(userDetails, orderId));
    }

    @Override
    @PutMapping("/return/{orderId}")
    public ResponseEntity<OrderResponseDto> requestReturn(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.requestReturn(userDetails, orderId));
    }
}
