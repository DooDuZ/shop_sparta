package com.sparta.shop_sparta.domain.entity.wishlist;

import com.sparta.shop_sparta.domain.entity.BaseEntity;
import com.sparta.shop_sparta.domain.entity.item.ItemEntity;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "wishList")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishListEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishListId;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "memberId")
    private MemberEntity memberEntity;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "itemId")
    private ItemEntity itemEntity;

    @Column(nullable = false)
    private Boolean isChecked;

    public WishListEntity(MemberEntity memberEntity, ItemEntity itemEntity, Boolean isChecked) {
        this.memberEntity = memberEntity;
        this.itemEntity = itemEntity;
        this.isChecked = isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }
}
