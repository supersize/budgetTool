package com.example.budgetTool.handler;

import com.example.budgetTool.service.RedisService;
import com.example.budgetTool.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName    : com.example.budgetTool.handler
 * author         : kimjaehyeong
 * date           : 11/27/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 11/27/25        kimjaehyeong       created
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutSuccessHandler {
    private final RedisService redisService;

    @Value("${token.access}")
    private String ACCESS_TOKEN_PRIFIX;

    @Value("${token.refresh}")
    private String REFRESH_TOKEN_PRIFIX;

    /**
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String token = JwtUtil.extractTokenFromRequest(request);
        String email = JwtUtil.getUserEmail(token);

        String accessToken = this.redisService.getSingleData(ACCESS_TOKEN_PRIFIX + email);
        String refreshToken = this.redisService.getSingleData(REFRESH_TOKEN_PRIFIX + email);

        this.redisService.deleteSingleData(accessToken);
        this.redisService.deleteSingleData(refreshToken);

        // Delete cookies on user's browser
        Cookie jwtCookie = new Cookie("accessToken", null); // 값은 null
        jwtCookie.setMaxAge(0);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true); // HTTPS 환경인 경우 필요
//        jwtCookie.setSameSite("Strict"); // SameSite 정책도 필요 시 설정

        response.addCookie(jwtCookie);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK); // HTTP 200 OK
        // Send JSON response
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Logout successful");

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
    }
}
