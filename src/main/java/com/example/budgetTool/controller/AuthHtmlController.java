package com.example.budgetTool.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthHtmlController {
    @GetMapping("/login")
    public String goToLoginPage (HttpServletRequest request, HttpServletResponse response) {

        return "/membership/login";
    }

    @GetMapping("/sign-up")
    public String goToSignUpPage (HttpServletRequest request, HttpServletResponse response) {

        return "/membership/sign-up";
    }

    @GetMapping("/forgot-password")
    public String goToForgotPasswordPage (HttpServletRequest request, HttpServletResponse response) {

        return "/membership/forgot-password";
    }

}
