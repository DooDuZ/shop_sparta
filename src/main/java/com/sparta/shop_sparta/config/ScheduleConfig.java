package com.sparta.shop_sparta.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScheduleConfig {

    @Bean
    Map<Long, ScheduledFuture<?>> scheduledTasks() {
        return new ConcurrentHashMap<>();
    }
}
