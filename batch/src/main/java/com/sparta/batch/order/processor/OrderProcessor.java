package com.sparta.batch.order.processor;

import com.sparta.batch.constant.OrderStatus;
import com.sparta.batch.domain.entity.order.OrderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderProcessor implements ItemProcessor<OrderEntity, OrderEntity> {
    @Override
    public OrderEntity process(OrderEntity orderEntity){
        orderEntity.setOrderStatus(OrderStatus.of(orderEntity.getOrderStatus().getStatus() + 1 ));

        return orderEntity;
    }
}
