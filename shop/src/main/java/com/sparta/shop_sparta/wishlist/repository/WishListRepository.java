package com.sparta.shop_sparta.wishlist.repository;

import com.sparta.shop_sparta.member.domain.entity.MemberEntity;
import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import com.sparta.shop_sparta.wishlist.domain.entity.WishListEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishListRepository extends JpaRepository<WishListEntity, Long> {
    Page<WishListEntity> findAllByMemberEntity(Pageable pageable, MemberEntity memberEntity);

    Optional<WishListEntity> findByProductEntityAndMemberEntity(ProductEntity productEntity, MemberEntity memberEntity);
}
