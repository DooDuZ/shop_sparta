package com.sparta.shop_sparta.service.order;


import com.sparta.shop_sparta.domain.dto.order.OrderDetailDto;
import com.sparta.shop_sparta.domain.dto.order.OrderDetailRequestDto;
import com.sparta.shop_sparta.domain.entity.order.OrderEntity;
import java.util.List;

public interface OrderDetailService {
    List<OrderDetailDto> addOrder(OrderEntity orderEntity, List<OrderDetailRequestDto> orderDetailRequstDtoList);
    List<OrderDetailDto> getOrderedProduct(OrderEntity orderEntity);
    void cancelOrder(OrderEntity orderEntity);
}
