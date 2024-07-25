package com.sparta.shop_sparta.member.service.verify;

public interface VerifySignUpService<T> {
    void verifySignup(Long id, String verificationCode);
    void sendVerification(T dto);
}
