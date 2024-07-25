package com.sparta.shop_sparta.wishlist.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Setter
public class WishListResponseDto {
    private Long wishlistId;
    private Long memberId;
    private Long productId;
}
