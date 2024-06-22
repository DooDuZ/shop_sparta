package com.sparta.shop_sparta.service.order;

import com.sparta.shop_sparta.domain.dto.order.OrderDto;
import com.sparta.shop_sparta.repository.OrderRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService{

    OrderRepository orderRepository;
    @Autowired
    OrderServiceImpl(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }
    @Override
    public OrderDto addOrder(OrderDto orderDTO) {
        // 유저 검증 로직
        // 회원제 만들고 돌아올게요...
        return orderDTO;
    }

    @Override
    public List<OrderDto> getOrders(Long memberId) {
        return null;
    }

    @Override
    public OrderDto cancelOrder(OrderDto orderDTO) {
        return null;
    }
}
