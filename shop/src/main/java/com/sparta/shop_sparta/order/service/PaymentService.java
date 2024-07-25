package com.sparta.shop_sparta.order.service;

import com.sparta.shop_sparta.order.domain.entity.OrderEntity;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    public Boolean pay(OrderEntity orderEntity) {
        if (orderEntity == null) {
            return false;
        }

        // 결제 실패 테스트용 임시 코드
        SecureRandom secureRandom = new SecureRandom();
        int number = secureRandom.nextInt(10);
        if (number >= 8) {
            return false;
        }
        // 결제 실패 테스트용 임시 코드

        // Todo 결제 로직 추가

        return true;
    }

    public Boolean cancel(OrderEntity orderEntity) {
        if (orderEntity == null) {
            return false;
        }

        // Todo 환불 로직 추가

        return true;
    }
}
