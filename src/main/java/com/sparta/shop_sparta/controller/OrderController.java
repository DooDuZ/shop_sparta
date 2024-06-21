package com.sparta.shop_sparta.controller;

import com.sparta.shop_sparta.domain.dto.order.OrderDTO;
import java.util.List;

public interface OrderController {
    OrderDTO addOrder();
    List<OrderDTO> getOrders();
    OrderDTO cancelOrder();
}
