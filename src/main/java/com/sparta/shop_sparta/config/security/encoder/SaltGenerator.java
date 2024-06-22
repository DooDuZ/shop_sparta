package com.sparta.shop_sparta.config.security.encoder;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

public class SaltGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    public String generateSalt() {
        byte[] saltBytes = new byte[4];  // 4 바이트 크기의 배열 생성
        secureRandom.nextBytes(saltBytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : saltBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();  // 8자 길이의 16진수 문자열 반환
    }
}
