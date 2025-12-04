package com.example.budgetTool.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * packageName    : com.example.budgetTool.controller
 * author         : kimjaehyeong
 * date           : 11/27/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 11/27/25        kimjaehyeong       created
 */
@Slf4j
@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public String processError (HttpServletRequest request, Model model) {
        Object status       = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message      = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object uri          = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        Object exception    = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        if(status == null) return "common/error";

        int statusCode = Integer.parseInt(status.toString());
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("message", message);
        model.addAttribute("uri", uri);
        model.addAttribute("exception", exception);

        return switch (statusCode) {
            case 404 -> "common/404";
            case 403 -> "common/403";
            case 500 -> "common/500";
            default -> "common/error";
        };
    }


    @GetMapping("/404")
    public String notFound(Model model) {
        model.addAttribute("statusCode", 404);
        model.addAttribute("message", "404 Not Found");
        model.addAttribute("time", LocalDateTime.now());

        return "common/404";
    }

    @GetMapping("/403")
    public String forbidden(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("statusCode", 403);
        model.addAttribute("message", "403 Forbidden");
        model.addAttribute("time", LocalDateTime.now());

        if(userDetails != null) {
            model.addAttribute("currentUser", userDetails.getUsername());
//            model.addAttribute("Authority", userDetails.getAuthorities());
        }

        return "common/403";
    }

    @GetMapping("/500")
    public String internalError(Model model) {
        model.addAttribute("statusCode", 500);
        model.addAttribute("message", "500 Internal Error");
        model.addAttribute("time", LocalDateTime.now());
        model.addAttribute("requestId", UUID.randomUUID().toString());
        return "common/500";
    }
}
