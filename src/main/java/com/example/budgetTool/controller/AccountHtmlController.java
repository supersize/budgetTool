package com.example.budgetTool.controller;

import org.springframework.stereotype.Controller;
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
    public String goToAccountListPage () {

        String test = "yoyo";

        return "board/dashboard";
    }
}
