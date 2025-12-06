package com.example.budgetTool.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
@Controller
public class AccountHtmlController {

    @GetMapping("/accounts")
    public String goToAccountListPage(HttpServletRequest request, Model model) {
        // Add currentPath to model for sidebar active state
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String currentPath = uri.substring(contextPath.length());
        
        model.addAttribute("currentPath", currentPath);
        
        return "account/account-list";
    }
}
