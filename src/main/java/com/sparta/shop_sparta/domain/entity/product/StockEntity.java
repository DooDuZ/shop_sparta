package com.sparta.shop_sparta.domain.entity.product;

import com.sparta.shop_sparta.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "stock")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StockEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockId;

    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long amount;

    @OneToOne
    @ToString.Exclude
    @JoinColumn(name = "productId")
    private ProductEntity productEntity;

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
