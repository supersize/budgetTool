package com.example.budgetTool.config;

import com.example.budgetTool.filter.JwtAuthenticationFilter;
import com.example.budgetTool.filter.LoginPageRedirectFilter;
import com.example.budgetTool.handler.AuthenticationHandler;
import com.example.budgetTool.handler.CustomLogoutHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.security.Principal;


/**
 * packageName    : com.example.budgetTool.config
 * author         : kimjaehyeong
 * date           : 11/10/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 11/10/25        kimjaehyeong       created
 */
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationHandler authenticationHandler;
    private final CustomLogoutHandler customLogoutHandler;
    private final LoginPageRedirectFilter loginPageRedirectFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("securityConfig!!!");

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(authenticationHandler)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(loginPageRedirectFilter, JwtAuthenticationFilter.class)
                .authorizeHttpRequests(authorizeRequests -> {
                    authorizeRequests
                        .requestMatchers("/login", "/Auth/**", "/sign-up", "/auth/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/bootstrap/**").permitAll()
                        .requestMatchers("/error", "/404", "/403", "/500").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated();
                })
                /*
                    formLogin() is mainly used in session-based login, so not proper for token-based login.
                    but it's okay for formLogin() to be used with SessionCreationPolicy.STATELESS and successHandler()
                 */
                .formLogin(form -> { // formLogin() 에서 authentication 객체 자동 생성됨
                    form
                        .loginPage("/login")
                        .loginProcessingUrl("/Auth/login")
                        .usernameParameter("email")
                        .passwordParameter("passwordHash")
                        .successHandler(authenticationHandler)
                        .failureHandler(authenticationHandler)
                    ;
                })
                .logout(logout -> {
                    logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(customLogoutHandler)
                    ;
                })
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint((request, response, authException) -> {
                        log.warn("Authentication failed for : {} - {}", request.getRequestURI(), authException.getMessage());

                        // AJAX 요청 확인
                        String ajaxHeader = request.getHeader("X-Requested-With");
                        if ("XMLHttpRequest".equals(ajaxHeader)) {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(
                                    "{\"error\": \"Unauthorized\", \"message\": \"" +
                                            authException.getMessage() + "\"}"
                            );
                        } else {
                            log.info("hahaha!!!");
                            // 일반 요청은 로그인 페이지로
                            response.sendRedirect(request.getContextPath() + "/auth/login");
                        }
                    })
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        log.warn("Access denied for: {} - {}",
                                request.getRequestURI(), accessDeniedException.getMessage());
                        response.sendRedirect(request.getContextPath() + "/403");
                    });
                })
                .build();
    }


}
