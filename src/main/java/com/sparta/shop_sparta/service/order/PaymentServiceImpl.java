package com.sparta.shop_sparta.service.order;

import com.sparta.shop_sparta.domain.entity.order.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{
    @Override
    public Boolean pay(OrderEntity orderEntity) {
        return true;
    }

    @Override
    public Boolean cancel(OrderEntity orderEntity) {
        return true;
    }
}
