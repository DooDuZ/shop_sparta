package com.sparta.shop_sparta.controller.order;

import com.sparta.shop_sparta.domain.dto.member.MemberDTO;
import com.sparta.shop_sparta.domain.dto.order.OrderDTO;
import com.sparta.shop_sparta.service.order.OrderService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderControllerImpl implements OrderController{

    OrderService orderService;
    @Autowired
    OrderControllerImpl(OrderService orderService){
        this.orderService = orderService;
    }

    @Override
    @PostMapping("/")
    public OrderDTO addOrder(@RequestBody OrderDTO orderDTO) {
        return orderService.addOrder(orderDTO);
    }

    @Override
    public List<OrderDTO> getOrders(MemberDTO memberDTO) {
        return null;
    }

    @Override
    public OrderDTO cancelOrder(@RequestBody OrderDTO orderDTO) {
        return null;
    }
}
