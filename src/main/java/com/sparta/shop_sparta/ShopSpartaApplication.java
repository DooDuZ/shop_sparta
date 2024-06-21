package com.sparta.shop_sparta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ShopSpartaApplication {
    private static final Logger logger = LoggerFactory.getLogger(ShopSpartaApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(ShopSpartaApplication.class, args);
        logger.info("Hello, World!");
    }
}
