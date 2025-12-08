package com.example.budgetTool.model.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * packageName    : com.example.budgetTool.model.dto
 * author         : kimjaehyeong
 * date           : 12/08/25
 * description    : Report DTO
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/08/25        kimjaehyeong       created
 */
public class ReportDto {

    /**
     * Monthly Summary
     */
    public record MonthlySummary(
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal netAmount,
            int transactionCount
    ) {}

    /**
     * Transaction Type Summary
     */
    public record TransactionTypeSummary(
            String type,
            BigDecimal amount,
            int count
    ) {}

    /**
     * Daily Transaction Summary
     */
    public record DailyTransactionSummary(
            String date,
            BigDecimal income,
            BigDecimal expense
    ) {}

    /**
     * Account Balance Summary
     */
    public record AccountBalanceSummary(
            String accountName,
            BigDecimal balance,
            String currency
    ) {}

    /**
     * Overall Report Response
     */
    public record ReportResponse(
            MonthlySummary monthlySummary,
            List<TransactionTypeSummary> transactionTypeSummary,
            List<DailyTransactionSummary> dailyTransactionSummary,
            List<AccountBalanceSummary> accountBalanceSummary
    ) {}
}
