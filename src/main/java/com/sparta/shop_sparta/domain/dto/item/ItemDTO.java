package com.sparta.shop_sparta.domain.dto.item;


import com.sparta.shop_sparta.domain.entity.item.ItemEntity;
import com.sparta.shop_sparta.domain.constant.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDTO {
    private Long itemId;
    private String itemName;
    private String itemDetails;
    private Long itemTypeID;
    private ItemStatus itemStatus;

    public ItemEntity toEntity(){
        return ItemEntity.builder().itemId(this.itemId).itemDetails(this.itemDetails)
                .itemStatus(this.itemStatus).itemName(this.itemName).build();
    }
}
