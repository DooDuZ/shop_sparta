package com.sparta.shop_sparta.domain.entity.item;

import com.sparta.shop_sparta.domain.entity.BaseEntity;
import com.sparta.shop_sparta.domain.entity.item.constant.ItemStatus;
import jakarta.persistence.CascadeType;
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

@Entity(name = "item")
@NoArgsConstructor
@Getter
public class ItemEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(nullable = false)
    private String itemName;

    @Lob
    @Column(nullable = false)
    private String item_details;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "itemTypeId")
    private ItemTypeEntity itemTypeEntity;

    @Column(nullable = false)
    private ItemStatus itemStatus;

    public ItemEntity(String itemName, String item_details, ItemTypeEntity itemTypeEntity, ItemStatus itemStatus) {
        this.itemName = itemName;
        this.item_details = item_details;
        this.itemTypeEntity = itemTypeEntity;
        this.itemStatus = itemStatus;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItem_details(String item_details) {
        this.item_details = item_details;
    }

    public void setItemTypeEntity(ItemTypeEntity itemTypeEntity) {
        this.itemTypeEntity = itemTypeEntity;
    }

    public void setItemStatus(ItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }
}
