package com.example.budgetTool.utils;

import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * packageName    : com.example.budgetTool.utils
 * author         : kimjaehyeong
 * date           : 11/8/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 11/8/25        kimjaehyeong       created
 */

public class ShaUtil {
    private static final int SALT_SIZE = 16;

    /**
     * getting a salt
     * @return
     * @throws Exception
     */
    public String getSalt () throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_SIZE];
        random.nextBytes(salt);

        return byteToString(salt);
    }


    /**
     * changing byte to String
     * @param param
     * @return
     */
    private String byteToString (byte[] param) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : param) {
            stringBuilder.append(String.format("%02x", b));
        }

        return stringBuilder.toString();
    }


    /**
     * getting a hash value
     * @param password
     * @param salt
     * @return
     * @throws Exception
     */
    public String getHash (byte[] password, String salt) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        for (int i = 0; i < 10000; i++) {
            String temp = byteToString(password) + salt;
            md.update(temp.getBytes());
            password = md.digest();
        }
        return byteToString(password);
    }
}
