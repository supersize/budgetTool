package com.example.budgetTool.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * packageName    : com.example.budgetTool.filter
 * author         : kimjaehyeong
 * date           : 11/29/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 11/29/25        kimjaehyeong       created
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginPageRedirectFilter extends OncePerRequestFilter {

    /**
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();

        if (contextPath != null && !contextPath.isEmpty())
            path = path.substring(contextPath.length());

        /*
            anonymousUser and isAuthenticated() == true means that you are a guest but has "Guest badge".
            so, you are authenticated as a guest.
         */
        // 로그인 페이지 요청인지 확인
        if (path.equals("/login")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 인증된 사용자이고, 익명 사용자가 아닌 경우
            if (authentication != null &&
                    authentication.isAuthenticated() &&
                    !"anonymousUser".equals(authentication.getPrincipal())) {

                log.info("Authenticated user tried to access login page. Redirecting to main.");
                response.sendRedirect(contextPath + "/main");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
