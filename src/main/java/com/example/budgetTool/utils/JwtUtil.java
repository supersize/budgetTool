package com.example.budgetTool.utils;

import groovy.util.logging.Slf4j;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;

/**
 * packageName    : com.example.budgetTool.utils
 * author         : kimjaehyeong
 * date           : 11/11/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 11/11/25        kimjaehyeong       created
 */
@Slf4j
public class JwtUtil {

    private static final String SECRET = "20251112-secret-token-key-yoyoyo-yayaya-hohoho";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);


    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String getUserEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(KEY).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static String generateToken(String userEmail) {
        return Jwts.builder()
//                .setHeaderParam("kid", "test")
                .setSubject(userEmail)
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30 mins
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 10)) // 10 sec
                .signWith(KEY)
                .compact();

    }

    public static String generateRefreshToken(String userEmail) {
        return Jwts.builder()
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // a week
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 )) // 24 hours
                .signWith(KEY)
                .compact();
    }


    public static boolean isValiedToken(String token) {
        if(token.isBlank()) return false;
        try{
            Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            log.error("[error] error occur while validating the token", e.getMessage());
        }

        return false;
    }

    public static boolean isTokenBlank(String token) {
        if(token == null || token.isBlank()) return false;
        return true;
    }



    public static String extractTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) return null;
        // 조회 되는 토큰 없을 시 pseudo 데이터 삽입
        String accessTokenFromCookie
                = Arrays.stream(cookies)
                .filter(item -> item.getName().equals("accessToken"))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if(accessTokenFromCookie != null)
            return accessTokenFromCookie;

        return null;
    }
}
