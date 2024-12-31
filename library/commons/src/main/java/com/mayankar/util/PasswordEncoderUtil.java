package com.mayankar.util;

import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Component
public class PasswordEncoderUtil {
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    public boolean matches(String password, String encodedPassword) {
        return bCryptPasswordEncoder.matches(password, encodedPassword);
    }

    public static void main(String[] args) {
        PasswordEncoderUtil passwordEncoderUtil = new PasswordEncoderUtil();
        String encodedPassword = passwordEncoderUtil.encodePassword("secret");
        System.out.println(encodedPassword);
    }
}
