package com.sparta.shop_sparta.controller;

import com.sparta.shop_sparta.domain.dto.order.OrderResponseDTO;
import java.util.List;

public interface OrderController {
    OrderResponseDTO addOrder();
    List<OrderResponseDTO> getOrders();
    OrderResponseDTO cancelOrder();
}
