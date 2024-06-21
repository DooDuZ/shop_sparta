package com.sparta.shop_sparta.domain.entity.item;

import com.sparta.shop_sparta.domain.dto.item.ItemDTO;
import com.sparta.shop_sparta.domain.entity.BaseEntity;
import com.sparta.shop_sparta.domain.constant.ItemStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "item")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(nullable = false)
    private String itemName;

    @Lob
    @Column(nullable = false)
    private String itemDetails;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "itemTypeId")
    private ItemTypeEntity itemTypeEntity;

    @Column(nullable = false)
    private ItemStatus itemStatus;

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItem_details(String item_details) {
        this.itemDetails = item_details;
    }

    public void setItemTypeEntity(ItemTypeEntity itemTypeEntity) {
        this.itemTypeEntity = itemTypeEntity;
    }

    public void setItemStatus(ItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }

    public ItemDTO toDTO(){
        return ItemDTO.builder().itemId(this.itemId).itemTypeID(this.itemTypeEntity.getItemTypeId()).itemDetails(this.itemDetails)
                .itemStatus(this.itemStatus).itemName(this.itemName).build();
    }
}
