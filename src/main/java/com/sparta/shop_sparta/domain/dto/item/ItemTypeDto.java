package com.sparta.shop_sparta.domain.dto.item;

import com.sparta.shop_sparta.domain.entity.item.ItemTypeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemTypeDto {
    private Long itemTypeId;
    private String itemTypeName;

    public ItemTypeEntity toEntity(){
        return ItemTypeEntity.builder().itemTypeId(this.itemTypeId).itemTypeName(this.itemTypeName).build();
    }
}
