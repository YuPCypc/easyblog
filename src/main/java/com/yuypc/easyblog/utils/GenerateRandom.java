package com.yuypc.easyblog.utils;

import java.util.Random;

public class GenerateRandom {

    public static String generateRandomText() {
        int length = 6;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder text = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            text.append(characters.charAt(random.nextInt(characters.length())));
        }

        return text.toString();
    }
}

