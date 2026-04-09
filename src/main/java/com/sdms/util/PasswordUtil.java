package com.sdms.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    public static String hash(String password) {
        try {
            SecureRandom sr = new SecureRandom();
            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashed = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hashed);
        } catch (Exception e) { throw new RuntimeException("Hashing failed", e); }
    }

    public static boolean verify(String password, String stored) {
        try {
            String[] parts = stored.split(":");
            if (parts.length != 2) return false;
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashed = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashed).equals(parts[1]);
        } catch (Exception e) { return false; }
    }
}
