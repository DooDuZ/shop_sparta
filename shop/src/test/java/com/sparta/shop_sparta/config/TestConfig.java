package com.sparta.shop_sparta.config;

import com.sparta.shop_sparta.common.domain.BaseEntity;
import com.sparta.shop_sparta.common.domain.BaseEntityListener;
import jakarta.persistence.PreRemove;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {
    @Bean
    @Primary
    public BaseEntityListener testBaseEntityListener() {
        return new TestBaseEntityListener();
    }

    public static class TestBaseEntityListener extends BaseEntityListener {
        @Override
        @PreRemove
        public void preRemove(BaseEntity entity) {
            // 테스트 환경에서는 아무 동작도 하지 않음
        }
    }
}
