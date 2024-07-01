package com.sparta.shop_sparta.repository;

import com.sparta.shop_sparta.domain.dto.cart.CartDetailResponseDto;
import com.sparta.shop_sparta.domain.entity.cart.CartDetailEntity;
import com.sparta.shop_sparta.domain.entity.cart.CartEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartDetailRepository extends JpaRepository<CartDetailEntity, Long> {
    void deleteByCartEntityAndProductEntity_ProductId(CartEntity cartEntity,Long productId);

    List<CartDetailEntity> findAllByCartEntity(CartEntity cartEntity);
}
