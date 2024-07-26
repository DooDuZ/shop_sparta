package com.sparta.shop_sparta.order.repository;

import com.sparta.common.constant.order.OrderStatus;
import com.sparta.shop_sparta.member.domain.entity.MemberEntity;
import com.sparta.shop_sparta.order.domain.entity.OrderEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByOrderStatus(OrderStatus orderStatus);

    @Modifying
    @Query(value = "UPDATE `order` o SET o.orderStatus = :nextStatus WHERE o.orderStatus = :prevStatus", nativeQuery = true)
    int updateOrderStatus(@Param("prevStatus") OrderStatus prevStatus, @Param("nextStatus") OrderStatus nextStatus);

    Page<OrderEntity> findAllByMemberEntity(Pageable pageable, MemberEntity memberEntity);
}
