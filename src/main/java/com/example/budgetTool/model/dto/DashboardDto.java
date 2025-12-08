package com.example.budgetTool.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * packageName    : com.example.budgetTool.model.dto
 * author         : kimjaehyeong
 * date           : 12/08/25
 * description    : Dashboard DTO
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/08/25        kimjaehyeong       created
 */
public class DashboardDto {

    /**
     * Dashboard Summary
     */
    public record DashboardSummary(
            int totalAccounts,
            BigDecimal totalBalance,
            BigDecimal monthlyGrowth,
            double monthlyGrowthPercentage,
            List<AccountOverview> accountOverviews,
            List<RecentTransaction> recentTransactions
    ) {}

    /**
     * Account Overview
     */
    public record AccountOverview(
            Long accountId,
            String bankName,
            String accountNumber,
            BigDecimal balance,
            String currency,
            double percentage
    ) {}

    /**
     * Recent Transaction
     */
    public record RecentTransaction(
            Long transactionId,
            String type,
            String title,
            String accountInfo,
            BigDecimal amount,
            String currency,
            LocalDateTime createdAt,
            boolean isIncome
    ) {}
}
