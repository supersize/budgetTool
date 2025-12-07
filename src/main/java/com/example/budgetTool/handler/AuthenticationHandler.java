package com.example.budgetTool.handler;

import com.example.budgetTool.model.entity.User;
import com.example.budgetTool.repository.UserRepository;
import com.example.budgetTool.service.RedisService;
import com.example.budgetTool.service.UserService;
import com.example.budgetTool.utils.JwtUtil;
import com.example.budgetTool.utils.ShaUtil;
import com.example.budgetTool.utils.querydsl.FieldCondition;
import com.example.budgetTool.utils.querydsl.LogicType;
import com.example.budgetTool.utils.querydsl.Operator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : com.example.budgetTool.handler
 * author         : kimjaehyeong
 * date           : 11/22/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 11/22/25        kimjaehyeong       created
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler, AuthenticationProvider {

    private final RedisService redisService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${token.access}")
    private String ACCESS_TOKEN_PRIFIX;

    @Value("${token.refresh}")
    private String REFRESH_TOKEN_PRIFIX;

    /**
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String rawPw = authentication.getCredentials().toString();

        List<FieldCondition> fconds = new ArrayList<>();
        fconds.add(new FieldCondition("email", Operator.EQ, email, LogicType.AND));
        User user = this.userService.getUser(fconds, null);

        if (user == null) {
            throw new BadCredentialsException("User not found");
        }

        String storedPw = user.getPasswordHash();
        log.info("Retrieved email, inputPw and storedPw for user. {}, {}, {}", email, rawPw, storedPw);

        boolean isSamePw = this.passwordEncoder.matches(rawPw, storedPw);
        if(!isSamePw) {
            log.error("[ERROR] Password does not match stored pw hashed.");
            throw new BadCredentialsException("Password does not match stored pw hashed.");
        }

        log.info("Password matched with input data.");
        return new UsernamePasswordAuthenticationToken(user, storedPw, user.getAuthorities());
    }

    /**
     * letting server know what kinds of authentication it can process.
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response
            , Authentication authentication) throws IOException, ServletException {
        if (!(authentication.getPrincipal() instanceof User)) {
            // This should not happen in a standard setup, but is a safe check.
            throw new IllegalStateException("Authentication principal is not a User instance.");
        }

        User user = (User) authentication.getPrincipal();
        String email = user.getEmail();

        // access token
        String accessToken = JwtUtil.generateToken(email);
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // "false" if dev mode
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60 * 24); // a day
        response.addCookie(accessTokenCookie);
        this.redisService.setSingleData(ACCESS_TOKEN_PRIFIX + email, accessToken);

        // refresh token
        // don't have to set refreshToken on cookie
//        String refreshToken = JwtUtil.generateRefreshToken(email);
//        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setSecure(true);
//        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7);
//        response.addCookie(refreshTokenCookie);
//        this.redisService.setSingleData(REFRESH_TOKEN_PRIFIX + email, refreshToken);

        //



        // Set response content type to JSON
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK); // HTTP 200 OK

        // Write JSON response
        String jsonResponse = String.format(
                "{\"status\": \"success\", \"message\": \"Login successful\", \"redirectUrl\": \"/main\"}",
                authentication.getName(),
                accessToken // Tokens sent here
//                , refreshToken
        );

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    /**
     * @param request
     * @param response
     * @param exception
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response
            , AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = "Wrong password. Please try again.";

        // 2. Get the specific class name (e.g., "BadCredentialsException")
        String exceptionType = exception.getClass().getSimpleName();

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized

        // 3. Write JSON response including the error details
        response.getWriter().write(String.format(
                "{\"status\": \"error\", \"message\": \"%s\", \"type\": \"%s\"}",
                errorMessage, exceptionType
        ));
        response.getWriter().flush();
    }
}
