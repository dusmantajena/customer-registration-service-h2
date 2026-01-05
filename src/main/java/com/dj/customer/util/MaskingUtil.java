package com.dj.customer.util;

public class MaskingUtil {

    // Mask email, show only first char & domain
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        String name = parts[0];
        if (name.length() <= 1) {
            return "*@" + parts[1];
        }
        return name.charAt(0) + "****@" + parts[1];
    }

    // Mask mobile, keep first 2 and last 2 digits
    public static String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 4) return mobile;
        return mobile.substring(0, 2) + "******" + mobile.substring(mobile.length() - 2);
    }

    // Generic mask: keep only first and last characters
    public static String maskGeneric(String value) {
        if (value == null || value.length() < 3) return "***";
        return value.charAt(0) + "***" + value.charAt(value.length() - 1);
    }
}
