package com.sparta.shop_sparta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@ComponentScan({"com.sparta.common", "com.sparta.shop_sparta"})
public class ShopSpartaApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopSpartaApplication.class, args);
    }
}
