package com.sparta.batch.order.processor;

import com.sparta.batch.domain.entity.order.OrderEntity;
import com.sparta.common.constant.order.OrderStatus;
import java.time.LocalDateTime;
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
        OrderStatus orderStatus = OrderStatus.of(orderEntity.getOrderStatus().getStatus() + 1);

        orderEntity.setOrderStatus(orderStatus);
        orderEntity.setLastModifyDate(LocalDateTime.now());

        return orderEntity;
    }
}
