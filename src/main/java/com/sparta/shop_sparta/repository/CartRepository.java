package com.sparta.shop_sparta.repository;

import com.sparta.shop_sparta.constant.cart.CartStatus;
import com.sparta.shop_sparta.domain.entity.cart.CartEntity;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByMemberEntityAndCartStatus(MemberEntity memberEntity, CartStatus cartStatus);
    Optional<CartEntity> findByMemberEntity_MemberIdAndCartStatus(Long memberId, CartStatus cartStatus);
}
