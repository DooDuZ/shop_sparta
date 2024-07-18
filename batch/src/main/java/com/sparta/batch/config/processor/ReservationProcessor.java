package com.sparta.batch.config.processor;

import com.sparta.batch.domain.entity.product.ReservationEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationProcessor implements ItemProcessor<ReservationEntity, ReservationEntity> {
    @Override
    public ReservationEntity process(ReservationEntity reservationEntity){
        reservationEntity.setCompleted(true);

        reservationEntity.getProductEntity().setProductStatus(reservationEntity.getReserveStatus());

        return reservationEntity;
    }
}
