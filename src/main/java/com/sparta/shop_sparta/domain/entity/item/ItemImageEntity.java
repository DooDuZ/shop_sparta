package com.sparta.shop_sparta.domain.entity.item;

import com.sparta.shop_sparta.domain.entity.BaseEntity;
import com.sparta.shop_sparta.domain.entity.item.constant.ItemImageType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "itemImage")
@Getter
@NoArgsConstructor
public class ItemImageEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemImageId;

    @Lob
    @Column(nullable = false)
    private String imagePath;

    @Column(nullable = false)
    private ItemImageType itemImageType;

    // 이미지 등록, 수정 등에서 순서를 계속 바꿀 수 있음
    @Column(nullable = false)
    private Byte imageOrdering;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "itemId")
    private ItemEntity itemEntity;

    public ItemImageEntity(String imagePath, ItemImageType itemImageType, Byte imageOrdering, ItemEntity itemEntity) {
        this.imagePath = imagePath;
        this.itemImageType = itemImageType;
        this.imageOrdering = imageOrdering;
        this.itemEntity = itemEntity;
    }

    public void setItemImageType(ItemImageType itemImageType) {
        this.itemImageType = itemImageType;
    }

    public void setImageOrdering(Byte imageOrdering) {
        this.imageOrdering = imageOrdering;
    }
}
