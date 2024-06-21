package com.sparta.shop_sparta.controller.order;

import com.sparta.shop_sparta.domain.dto.member.MemberDTO;
import com.sparta.shop_sparta.domain.dto.order.OrderDTO;
import java.util.List;

public interface OrderController {
    OrderDTO addOrder(OrderDTO orderDTO);
    List<OrderDTO> getOrders(MemberDTO memberDTO);
    OrderDTO cancelOrder(OrderDTO orderDTO);
}
