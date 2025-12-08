package com.example.budgetTool.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * packageName    : com.example.budgetTool.controller
 * author         : kimjaehyeong
 * date           : 12/8/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/8/25        kimjaehyeong       created
 */
@Controller
public class TransactionHtmlController {
    @GetMapping("/transactions")
    public String transactionHistory(Model model) {
        model.addAttribute("currentPath", "/transactions");
        return "transaction/transaction-history";
    }

}
