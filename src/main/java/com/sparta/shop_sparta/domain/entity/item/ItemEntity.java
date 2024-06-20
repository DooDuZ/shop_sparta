package com.sparta.shop_sparta.domain.entity.item;

import com.sparta.shop_sparta.domain.entity.BaseEntity;
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
    // item_status enum('NOT_PUBLISHED', 'Wating', 'on_sale', 'sale_ended', 'sale_suspended'),
}
