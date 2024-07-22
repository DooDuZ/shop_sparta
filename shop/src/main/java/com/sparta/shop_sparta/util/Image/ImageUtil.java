package com.sparta.shop_sparta.util.Image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class ImageUtil {
    public static byte[] readImage(String filepath) throws IOException {
        Path path = Paths.get(filepath);
        return Files.readAllBytes(path);
    }

    public static String encodeImageToBase64(byte[] imageData) {
        return Base64.getEncoder().encodeToString(imageData);
    }

    public static String readAndEncodeImage(String filepath) throws IOException {
        byte[] imageData = readImage(filepath);
        return encodeImageToBase64(imageData);
    }
}
