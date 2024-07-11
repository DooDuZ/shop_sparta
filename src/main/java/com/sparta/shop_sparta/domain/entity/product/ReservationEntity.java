package com.sparta.shop_sparta.domain.entity.product;

import com.sparta.shop_sparta.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "reservation")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @Column(nullable = false)
    private LocalDateTime openDateTime;
    @Column(nullable = false)
    private boolean completed;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "productId")
    private ProductEntity productEntity;

    public void setOpenDateTime(LocalDateTime openDateTime) {
        this.openDateTime = openDateTime;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
