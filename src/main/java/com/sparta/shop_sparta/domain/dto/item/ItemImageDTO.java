package com.sparta.shop_sparta.domain.dto.item;


import com.sparta.shop_sparta.domain.entity.item.ItemImageEntity;
import com.sparta.shop_sparta.domain.constant.ItemImageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemImageDTO {
    private Long itemImageId;
    private String imagePath;
    private Byte imageOrdering;

    private Long itemId;
    private ItemImageType itemImageType;

    public ItemImageEntity toEntity(){
        return ItemImageEntity.builder().itemImageId(this.itemImageId).imagePath(this.imagePath).imageOrdering(this.imageOrdering)
                .itemImageType(this.itemImageType).build();
    }
}
