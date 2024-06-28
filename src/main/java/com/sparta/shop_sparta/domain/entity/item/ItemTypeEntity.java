package com.sparta.shop_sparta.domain.entity.item;

import com.sparta.shop_sparta.domain.dto.item.ItemTypeDto;
import com.sparta.shop_sparta.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "itemType")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemTypeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemTypeId;

    @Column(nullable = false)
    private String itemTypeName;

    public ItemTypeEntity(String itemTypeName) {
        this.itemTypeName = itemTypeName;
    }

    public void setItemTypeName(String itemName) {
        this.itemTypeName = itemName;
    }

    public ItemTypeDto toDto(){
        return ItemTypeDto.builder().itemTypeId(this.itemTypeId).itemTypeName(this.itemTypeName).build();
    }
}
