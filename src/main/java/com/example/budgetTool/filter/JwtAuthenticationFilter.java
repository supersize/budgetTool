package com.example.budgetTool.filter;

import com.example.budgetTool.service.RedisService;
import com.example.budgetTool.service.UserService;
import com.example.budgetTool.utils.JwtUtil;
import jakarta.mail.AuthenticationFailedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * packageName    : com.example.budgetTool.filter
 * author         : kimjaehyeong
 * date           : 11/11/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 11/11/25        kimjaehyeong       created
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RedisService redisService;
    private final UserService userService;

    @Value("${token.access}")
    private String ACCESS_TOKEN_PRIFIX;

    @Value("${token.refresh}")
    private String REFRESH_TOKEN_PRIFIX;

    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/Auth",
            "/auth",
            "/sign-up",
            "/css",
            "/js",
            "/images",
            "/bootstrap",
            "/Auth",
            "/favicon.ico"
    );

    /**
     * @param request
     * @return
     * @throws ServletException
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        String method = request.getMethod();


        if (contextPath != null && !contextPath.isEmpty())
            path = path.substring(contextPath.length());

        // POST /Auth/login은 제외 (로그인 요청)
        if ("POST".equals(method) && path.startsWith("/Auth"))
            return true;

        // GET /login은 필터 통과 (인증 체크 필요)
        if ("GET".equals(method) && path.equals("/login"))
            return false;  // 필터를 거치도록

        // Check if path matches any excluded paths
        final String fixedPath = path;
        boolean shouldSkip = EXCLUDED_PATHS.stream()
                .anyMatch(excludedPath -> fixedPath.startsWith(excludedPath));

        log.info("This request's path of shouldNotFilter(). {}", path + " --> result : " + shouldSkip);

        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response
            , FilterChain filterChain) throws ServletException, IOException {
        try {
            String currentAccessToken = JwtUtil.extractTokenFromRequest(request);
            // 토큰이 없으면 필터 체인 계속 진행 (다른 필터나 컨트롤러에서 처리)
            if (currentAccessToken == null) {
                log.debug("No JWT token found in request");
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰이 유효하지 않으면 예외 던지기 (리다이렉트 X)
            if (!JwtUtil.isValiedToken(currentAccessToken)) {
                log.error("JWT token is not valid");
                throw new AuthenticationFailedException("Invalid JWT token");
            }

            String userEmail = JwtUtil.getUserEmail(currentAccessToken);

            String storedAccessToken = this.redisService.getSingleData(ACCESS_TOKEN_PRIFIX + userEmail);
            String storedRefreshToken = this.redisService.getSingleData(REFRESH_TOKEN_PRIFIX + userEmail);
            if(storedAccessToken == null || storedRefreshToken == null) {
                log.error("JWT token does not exist in Redis for user: {}", userEmail);
                throw new AuthenticationFailedException("JWT token does not exist.");
            }

            //TODO generate new A,R tokens
            if(!currentAccessToken.equals(storedAccessToken)){
                log.error("JWT token does not match for user: {}", userEmail);
                throw new AuthenticationFailedException("JWT token does not match.");
            }

            UserDetails userDetails = this.userService.loadUserByUsername(userEmail);
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authenticationToken.setDetails(userDetails);

            // Set authentication in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            log.info("Authentication is successed");
        } catch (AuthenticationFailedException e){
            log.error("Authentication failed: {}", e.getMessage());
            // SecurityContext를 비우고 예외를 전파
            SecurityContextHolder.clearContext();
            // 예외를 던지지 말고 필터 체인을 계속 진행하여
            // AuthenticationEntryPoint에서 처리하도록 함
        } catch (Exception e) {
            log.error("Can't set user authentication : ", e);
        }

        filterChain.doFilter(request, response);
    }



}
