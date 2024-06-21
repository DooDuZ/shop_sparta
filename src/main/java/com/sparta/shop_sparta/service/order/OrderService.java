package com.sparta.shop_sparta.service.order;

import com.sparta.shop_sparta.domain.dto.member.MemberDTO;
import com.sparta.shop_sparta.domain.dto.order.OrderDTO;
import java.util.List;

public interface OrderService {
    OrderDTO addOrder(OrderDTO orderDTO);
    List<OrderDTO> getOrders(Long memberId);
    OrderDTO cancelOrder(OrderDTO orderDTO);
}
