package com.sparta.shop_sparta.domain.entity.item;

import com.sparta.shop_sparta.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "itemType")
@NoArgsConstructor
@Getter
public class ItemTypeEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemTypeId;

    @Column(nullable = false)
    private String itemName;

    public ItemTypeEntity(String itemName) {
        this.itemName = itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
