package com.example.budgetTool.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * packageName    : com.example.budgetTool.controller.view
 * author         : kimjaehyeong
 * date           : 12/08/25
 * description    : Settings View Controller
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/08/25        kimjaehyeong       created
 */
@Controller
@RequestMapping("/settings")
public class SettingsViewController {

    @GetMapping
    public String settings(Model model) {
        model.addAttribute("currentPath", "/settings");
        return "settings/settings";
    }
}
