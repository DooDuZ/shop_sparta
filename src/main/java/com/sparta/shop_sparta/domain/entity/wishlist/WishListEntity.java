package com.sparta.shop_sparta.domain.entity.wishlist;

import com.sparta.shop_sparta.domain.entity.BaseEntity;
import com.sparta.shop_sparta.domain.entity.item.ItemEntity;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "wishList")
@NoArgsConstructor
@Getter
public class WishListEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishListId;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "memberId")
    MemberEntity memberEntity;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "itemId")
    ItemEntity itemEntity;
}
