package com.sparta.shop_sparta.config.security.encoder;

public interface EnableDecoding extends DataEncoder{
    String decrypt(String encodeData, String salt);
}
