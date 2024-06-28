package com.sparta.shop_sparta.util.encoder;

import jakarta.annotation.PostConstruct;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;

public class TokenUsernameEncoder {

    private final String ALGORITHM = "AES";

    private SecretKey secretKey;
    @Value("${TOKEN_USERNAME_SECRET}")
    private String tokenUsernameSecret;

    @PostConstruct
    private void init() throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = tokenUsernameSecret.getBytes();
        keyBytes = sha.digest(keyBytes);
        keyBytes = Arrays.copyOf(keyBytes, 32);

        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    // 암호화 메서드
    public String encrypt(String username) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(username.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // 복호화 메서드
    public String decrypt(String encryptUsername) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = Base64.getDecoder().decode(encryptUsername);
        byte[] originalBytes = cipher.doFinal(decryptedBytes);
        return new String(originalBytes);
    }
}
