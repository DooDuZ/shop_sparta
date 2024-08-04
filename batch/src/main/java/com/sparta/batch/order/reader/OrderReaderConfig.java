package com.sparta.batch.order.reader;

import com.sparta.batch.domain.entity.order.OrderEntity;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class OrderReaderConfig {
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    @StepScope
    public JpaPagingItemReader<OrderEntity> orderReader(
            @Value("#{jobParameters['date']}") LocalDateTime dateTime,
            @Value("#{jobParameters['chunkSize']}") int chunkSize
    ) {
        log.info(Thread.currentThread().getName() + ": Reading orders from database");
        JpaPagingItemReader<OrderEntity> reader = new JpaPagingItemReader<>();
        reader.setQueryString(
                "SELECT o FROM orders o " +
                        "WHERE o.lastModifyDate <= :date " +
                        "AND o.orderStatus IN ('PREPARED', 'IN_DELIVERY', 'RETURN_REQUESTED', 'IN_RETURN') " +
                        "ORDER BY o.orderId"
        );
        reader.setParameterValues(Collections.singletonMap("date", dateTime));
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setPageSize(chunkSize * chunkSize);
        reader.setSaveState(false);

        return reader;
    }
}
