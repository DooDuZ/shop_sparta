package com.sparta.shop_sparta.repository;

import com.sparta.shop_sparta.domain.entity.cart.CartDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartDetailRepository extends JpaRepository<CartDetailEntity, Long> {
}
