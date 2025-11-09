package com.example.budgetTool.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 */
@Slf4j
@Controller
public class MainHtmlController {

    @GetMapping("/login")
    public String goToLoginPage (HttpServletRequest request, HttpServletResponse response) {

        return "/membership/login";
    }

    @GetMapping("/sign-up")
    public String goToSignUpPage (HttpServletRequest request, HttpServletResponse response) {

        return "/membership/sign-up";
    }

    @GetMapping("/main")
    public String goToDashboard (HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("currentPath", request.getServletPath());

        return "board/dashboard";
    }

    @GetMapping("/")
    public String goToMainPage (HttpServletRequest request, HttpServletResponse response, Model model) {
        return "redirect:/main";
    }

}
