package com.example.budgetTool.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
@Slf4j
@Component
public class ShaUtil {
    private static final int SALT_SIZE = 16;

    /**
     * generating a salt
     * @return
     * @throws Exception
     */
    public String generateSalt () throws Exception {
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
     * Hash pw is single way so you can't decode the hashed value!
     * @param password
     * @param salt
     * @return
     * @throws Exception
     */
    public String getHash (byte[] password, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        for (int i = 0; i < 10000; i++) {
            String temp = byteToString(password) + salt;
            md.update(temp.getBytes());
            password = md.digest();
        }
        return byteToString(password);
    }
}
