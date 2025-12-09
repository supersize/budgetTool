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
@RequestMapping("/settings")
public class SettingsHtmlController {

    @GetMapping
    public String settings(Model model) {
        model.addAttribute("currentPath", "/settings");
        return "settings/settings";
    }
}
