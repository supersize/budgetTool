package com.example.budgetTool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * packageName    : com.example.budgetTool.config
 * author         : kimjaehyeong
 * date           : 12/1/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/1/25        kimjaehyeong       created
 */
@Configuration
public class PasswordConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        // BCrypt is better for password
        // http.passwordParameter("passwordHash") will be automatically encoded by BCrypt.
        return new BCryptPasswordEncoder();
    }
}
