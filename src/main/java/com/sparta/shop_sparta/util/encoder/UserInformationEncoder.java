package com.sparta.shop_sparta.util.encoder;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

public class UserInformationEncoder implements EnableDecoding{
    public final String secretKey;

    public UserInformationEncoder(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String encrypt(String data, String salt) {
        TextEncryptor textEncryptor = Encryptors.text(secretKey, salt);
        return textEncryptor.encrypt(data);
    }

    @Override
    public String decrypt(String encryptedData, String salt) {
        TextEncryptor textEncryptor = Encryptors.text(secretKey, salt);
        return textEncryptor.decrypt(encryptedData);
    }
}
