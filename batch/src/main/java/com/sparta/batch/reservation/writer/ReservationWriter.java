package com.sparta.batch.reservation.writer;

import com.sparta.batch.domain.entity.product.ReservationEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sparta.batch.repository.ReservationRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationWriter implements ItemWriter<ReservationEntity> {
    private final ReservationRepository reservationRepository;

    @Override
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void write(Chunk<? extends ReservationEntity> chunk) throws Exception {
        try {
            reservationRepository.saveAll(chunk.getItems());
        } catch (Exception e) {
            // 로그 출력 및 예외 처리
            log.error("Error writing items to database", e);
            throw e;
        }
    }
}

