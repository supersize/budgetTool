package com.example.budgetTool.controller;

import com.example.budgetTool.model.dto.DashboardDto;
import com.example.budgetTool.service.DashboardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class MainHtmlController {

    private final DashboardService dashboardService;

    @GetMapping("/main")
    public String goToDashboard (HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("currentPath", request.getServletPath());

        try {
            // Get current user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                org.springframework.security.core.userdetails.User userDetails = 
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
                
                // For now, we'll need to get userId from somewhere. You might need to modify this based on your UserDetails implementation
                // Assuming you have a custom UserDetails that has getUserId() method
                Long userId = getUserIdFromAuthentication(authentication);
                
                if (userId != null) {
                    DashboardDto.DashboardSummary summary = dashboardService.getDashboardSummary(userId);
                    
                    // Add data to model
                    model.addAttribute("totalAccounts", summary.totalAccounts());
                    model.addAttribute("totalBalance", formatCurrency(summary.totalBalance()));
                    model.addAttribute("monthlyChange", summary.monthlyGrowth());
                    model.addAttribute("monthlyGrowth", String.format("%+.1f%%", summary.monthlyGrowthPercentage()));
                    model.addAttribute("monthlyChangeText", formatCurrencyChange(summary.monthlyGrowth()));
                    
                    // Format accounts for display
                    List<Map<String, Object>> accounts = summary.accountOverviews().stream()
                        .map(account -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("bankName", account.bankName());
                            map.put("accountNumberLast4", account.accountNumber().substring(Math.max(0, account.accountNumber().length() - 4)));
                            map.put("formattedBalance", formatCurrency(account.balance()));
                            map.put("balancePercentage", account.percentage());
                            return map;
                        })
                        .collect(Collectors.toList());
                    model.addAttribute("accounts", accounts);
                    
                    // Format transactions for display
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    List<Map<String, Object>> transactions = summary.recentTransactions().stream()
                        .map(transaction -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("description", transaction.title());
                            map.put("accountInfo", transaction.accountInfo());
                            map.put("formattedDateTime", transaction.createdAt().format(formatter));
                            map.put("formattedAmount", (transaction.isIncome() ? "+" : "-") + formatCurrency(transaction.amount()));
                            map.put("amountClass", transaction.isIncome() ? "text-success" : "text-danger");
                            map.put("typeClass", getTransactionTypeClass(transaction.type()));
                            map.put("iconClass", getTransactionIconClass(transaction.type()));
                            return map;
                        })
                        .collect(Collectors.toList());
                    model.addAttribute("recentTransactions", transactions);
                } else {
                    // No userId found - show empty state
                    setEmptyDashboard(model);
                }
            } else {
                // Not authenticated - show empty state
                setEmptyDashboard(model);
            }
        } catch (Exception e) {
            log.error("Error loading dashboard", e);
            // On error, show empty state
            setEmptyDashboard(model);
        }

        return "board/dashboard";
    }

    @GetMapping("/")
    public String goToMainPage (HttpServletRequest request, HttpServletResponse response, Model model) {
        return "redirect:/main";
    }

    /**
     * Set empty dashboard attributes
     */
    private void setEmptyDashboard(Model model) {
        model.addAttribute("totalAccounts", 0);
        model.addAttribute("accounts", List.of());
        model.addAttribute("recentTransactions", List.of());
    }

    /**
     * Get user ID from authentication
     * TODO: Implement this based on your UserDetails implementation
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        // This is a placeholder. You need to implement this based on your actual UserDetails class
        // For example, if you have CustomUserDetails with getUserId():
        // if (authentication.getPrincipal() instanceof CustomUserDetails) {
        //     return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        // }
        
        // For now, return null to show empty state
        // You should implement proper user ID retrieval
        return null;
    }

    /**
     * Format currency
     */
    private String formatCurrency(BigDecimal amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.UK);
        return formatter.format(amount);
    }

    /**
     * Format currency change
     */
    private String formatCurrencyChange(BigDecimal amount) {
        String sign = amount.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
        return sign + formatCurrency(amount) + " (this month)";
    }

    /**
     * Get transaction type CSS class
     */
    private String getTransactionTypeClass(String type) {
        return switch (type) {
            case "DEPOSIT", "TRANSFER_IN" -> "bg-success";
            case "WITHDRAWAL", "TRANSFER_OUT" -> "bg-danger";
            default -> "bg-primary";
        };
    }

    /**
     * Get transaction icon CSS class
     */
    private String getTransactionIconClass(String type) {
        return switch (type) {
            case "DEPOSIT" -> "bi-arrow-down-left";
            case "WITHDRAWAL" -> "bi-arrow-up-right";
            case "TRANSFER_OUT" -> "bi-arrow-right";
            case "TRANSFER_IN" -> "bi-arrow-left";
            default -> "bi-credit-card";
        };
    }
}
