package com.sparta.shop_sparta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ShopSpartaApplication {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        SpringApplication.run(ShopSpartaApplication.class, args);
    }
}
