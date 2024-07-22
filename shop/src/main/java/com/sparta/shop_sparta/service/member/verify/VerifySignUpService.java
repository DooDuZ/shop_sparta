package com.sparta.shop_sparta.service.member.verify;

public interface VerifySignUpService<T> {
    void verifySignup(Long id, String verificationCode);
    void sendVerification(T dto);
}
