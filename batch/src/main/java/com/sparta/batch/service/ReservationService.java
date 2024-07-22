package com.sparta.batch.service;

import com.sparta.batch.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    /*@Transactional
    public void updateAllProductStatus(LocalDateTime dateTime){
        List<ReservationEntity> reservationEntities = reservationRepository.findAllByCompletedFalseAndOpenDateTimeLessThanEqual(dateTime);

        for(ReservationEntity reservationEntity : reservationEntities){
            reservationEntity.setCompleted(true);
            // product Service 의 책임 아닐까
            reservationEntity.getProductEntity().setProductStatus(reservationEntity.getReserveStatus());
        }
    }*/
}
