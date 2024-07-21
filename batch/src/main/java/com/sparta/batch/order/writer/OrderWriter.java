package com.sparta.batch.order.writer;

import com.sparta.batch.domain.entity.order.OrderEntity;
import com.sparta.batch.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class OrderWriter implements ItemWriter<OrderEntity> {
    private final OrderRepository orderRepository;

    @Override
    public void write(Chunk<? extends OrderEntity> chunk) throws Exception {
        try {
            orderRepository.saveAll(chunk.getItems());
        } catch (Exception e) {
            // 로그 출력 및 예외 처리
            log.error("Error writing items to database", e);
            throw e;
        }
    }
}
