package com.sparta.shop_sparta.service.order;

import com.sparta.shop_sparta.domain.entity.order.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{
    @Override
    public Boolean pay(OrderEntity orderEntity) {
        if (orderEntity == null) {
            return false;
        }

        // Todo 결제 로직 추가

        return true;
    }

    @Override
    public Boolean cancel(OrderEntity orderEntity) {
        if (orderEntity == null) {
            return false;
        }

        // Todo 환불 로직 추가

        return true;
    }
}
