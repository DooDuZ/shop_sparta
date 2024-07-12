package com.sparta.shop_sparta.service.order;

import com.sparta.shop_sparta.domain.dto.order.OrderDetailDto;
import com.sparta.shop_sparta.domain.dto.order.OrderDetailRequestDto;
import com.sparta.shop_sparta.domain.entity.order.OrderDetailEntity;
import com.sparta.shop_sparta.domain.entity.order.OrderEntity;
import java.util.List;

public interface OrderDetailService {
    List<OrderDetailEntity> addOrder(OrderEntity orderEntity, List<OrderDetailRequestDto> orderDetailRequstDtoList);
    List<OrderDetailDto> getOrderedProduct(OrderEntity orderEntity);
    void cancelOrder(OrderEntity orderEntity);
    void orderDetailSaveAll(List<OrderDetailEntity> orderDetailEntityList);
    List<OrderDetailEntity> getOrderDetailsByOrderEntities(List<OrderEntity> orderEntities);
    void rollbackOrder(List<OrderDetailEntity> orderDetailEntityList);
}
