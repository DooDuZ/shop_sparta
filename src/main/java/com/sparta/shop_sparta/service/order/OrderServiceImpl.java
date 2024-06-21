package com.sparta.shop_sparta.service.order;

import com.sparta.shop_sparta.domain.dto.order.OrderDTO;
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
    public OrderDTO addOrder(OrderDTO orderDTO) {
        // 유저 검증 로직
        // 회원제 만들고 돌아올게요...
        return orderDTO;
    }

    @Override
    public List<OrderDTO> getOrders(Long memberId) {
        return null;
    }

    @Override
    public OrderDTO cancelOrder(OrderDTO orderDTO) {
        return null;
    }
}
