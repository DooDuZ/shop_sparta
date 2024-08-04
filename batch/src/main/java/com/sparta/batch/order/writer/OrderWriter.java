package com.sparta.batch.order.writer;

import com.sparta.batch.domain.entity.order.OrderEntity;
import com.sparta.batch.repository.OrderRepository;
import com.sparta.common.constant.order.OrderStatus;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class OrderWriter implements ItemWriter<OrderEntity> {
    private final OrderRepository orderRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void write(Chunk<? extends OrderEntity> chunk) throws Exception {
        /*log.info("write - "+Thread.currentThread().getName() + ": Reading orders from database");
        try {
            orderRepository.saveAll(chunk.getItems());
        } catch (Exception e) {
            // 로그 출력 및 예외 처리
            log.error("Error writing items to database", e);
            throw e;
        }*/

        log.info("write - " + Thread.currentThread().getName() + ": Writing orders to database");
        List<OrderEntity> items = (List<OrderEntity>) chunk.getItems();

        jdbcTemplate.batchUpdate(
                "UPDATE orders SET order_status = ?, last_modify_date = ? WHERE order_id = ?",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        OrderEntity order = items.get(i);
                        ps.setString(1, String.valueOf(OrderStatus.of(order.getOrderStatus().getStatus())));
                        ps.setObject(2, LocalDateTime.now());
                        ps.setLong(3, order.getOrderId());
                    }

                    @Override
                    public int getBatchSize() {
                        return items.size();
                    }
                }
        );
    }
}
