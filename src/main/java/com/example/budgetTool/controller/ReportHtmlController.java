package com.example.budgetTool.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * packageName    : com.example.budgetTool.controller
 * author         : kimjaehyeong
 * date           : 12/9/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/9/25        kimjaehyeong       created
 */
@Controller
@RequestMapping("/reports")
public class ReportHtmlController {

    @GetMapping
    public String reports(Model model) {
        model.addAttribute("currentPath", "/reports");
        return "report/reports";
    }
}
