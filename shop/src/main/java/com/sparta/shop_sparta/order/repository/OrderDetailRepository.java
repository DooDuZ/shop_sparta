package com.sparta.shop_sparta.order.repository;

import com.sparta.shop_sparta.order.domain.entity.OrderDetailEntity;
import com.sparta.shop_sparta.order.domain.entity.OrderEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {
    List<OrderDetailEntity> findByOrderEntity(OrderEntity orderEntity);
    List<OrderDetailEntity> findAllByOrderEntityIn(List<OrderEntity> orderEntities);
}
