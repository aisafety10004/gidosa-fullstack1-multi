package net.gidosa.common.utils;

import java.util.Random;

public final class PasswordUtil {
    public static String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder tempPassword = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 12; i++) {
            tempPassword.append(chars.charAt(random.nextInt(chars.length())));
        }

        return tempPassword.toString();
    }
}
