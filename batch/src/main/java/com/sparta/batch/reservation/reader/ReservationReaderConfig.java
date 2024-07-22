package com.sparta.batch.reservation.reader;

import com.sparta.batch.domain.entity.product.ReservationEntity;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ReservationReaderConfig {
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    @StepScope
    public JpaPagingItemReader<ReservationEntity> reservationReader(
            @Value("#{jobParameters['date']}") LocalDateTime dateTime,
            @Value("#{jobParameters['chunkSize']}") int chunkSize
    ) {
        JpaPagingItemReader<ReservationEntity> reader = new JpaPagingItemReader<>();
        reader.setQueryString(
                "SELECT r FROM reservation r WHERE r.completed = false AND r.reservationTime <= :date ORDER BY r.reservationId"
        );
        reader.setParameterValues(Collections.singletonMap("date", dateTime));
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setPageSize(chunkSize); // jobParameters로부터 가져온 chunk 크기로 설정
        reader.setSaveState(false);
        return reader;
    }
}
