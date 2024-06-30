package com.sparta.shop_sparta.service.order;

import com.sparta.shop_sparta.domain.entity.order.OrderEntity;

public interface PaymentService {
    Boolean pay(OrderEntity orderEntity);
    Boolean cancel(OrderEntity orderEntity);
}
