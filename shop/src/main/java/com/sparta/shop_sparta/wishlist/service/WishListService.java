package com.sparta.shop_sparta.wishlist.service;

import com.sparta.common.constant.member.AuthMessage;
import com.sparta.common.exception.AuthorizationException;
import com.sparta.shop_sparta.member.domain.entity.MemberEntity;
import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import com.sparta.shop_sparta.product.service.ProductService;
import com.sparta.shop_sparta.wishlist.domain.dto.WishListRequestDto;
import com.sparta.shop_sparta.wishlist.domain.dto.WishListResponseDto;
import com.sparta.shop_sparta.wishlist.domain.entity.WishListEntity;
import com.sparta.shop_sparta.wishlist.repository.WishListRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishListService {
    private final WishListRepository wishListRepository;
    private final ProductService customerProductService;

    @Transactional
    public WishListResponseDto addWishList(UserDetails userDetails, WishListRequestDto wishListRequestDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        ProductEntity productEntity = customerProductService.getProductEntity(wishListRequestDto.getProductId());

        return wishListRepository.save(
                WishListEntity.builder()
                        .memberEntity(memberEntity)
                        .productEntity(productEntity)
                        .build()
        ).toDto();
    }

    public List<WishListResponseDto> getWishList(
            UserDetails userDetails,
            int page,
            int itemsPerPage
    ) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        Pageable pageable = PageRequest.of(page-1, itemsPerPage);

        return wishListRepository.findAllByMemberEntity(pageable, memberEntity).getContent()
                .stream().map(WishListEntity::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteWishList(UserDetails userDetails, Long productId) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        ProductEntity productEntity = customerProductService.getProductEntity(productId);

        WishListEntity wishListEntity = wishListRepository.findByProductEntityAndMemberEntity(productEntity, memberEntity).orElseThrow(
                () -> new RuntimeException("WishList Not Found")
        );

        if (wishListEntity.getMemberEntity().getMemberId() - memberEntity.getMemberId() != 0){
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED);
        }

        wishListRepository.delete(wishListEntity);
    }
}
