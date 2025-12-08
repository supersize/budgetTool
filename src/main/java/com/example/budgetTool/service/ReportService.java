package com.example.budgetTool.service;

import com.example.budgetTool.model.dto.ReportDto;
import com.example.budgetTool.model.entity.Account;
import com.example.budgetTool.model.entity.Transaction;
import com.example.budgetTool.model.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * packageName    : com.example.budgetTool.service
 * author         : kimjaehyeong
 * date           : 12/08/25
 * description    : Report Service
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/08/25        kimjaehyeong       created
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService {

    private final TransactionService transactionService;
    private final AccountService accountService;

    /**
     * Generate report for user
     * @param userId User ID
     * @return Report response
     */
    public ReportDto.ReportResponse generateReport(Long userId) {
        // Get current month transactions
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now().withDayOfMonth(1).plusMonths(1).minusSeconds(1);
        
        List<Transaction> transactions = transactionService.getTransactionListByDateRange(
                userId, startOfMonth, endOfMonth);

        // Get all user accounts
        List<Account> accounts = accountService.getAccountListByUserId(userId);

        // Generate summaries
        ReportDto.MonthlySummary monthlySummary = generateMonthlySummary(transactions);
        List<ReportDto.TransactionTypeSummary> typeSummary = generateTransactionTypeSummary(transactions);
        List<ReportDto.DailyTransactionSummary> dailySummary = generateDailyTransactionSummary(transactions);
        List<ReportDto.AccountBalanceSummary> accountSummary = generateAccountBalanceSummary(accounts);

        return new ReportDto.ReportResponse(
                monthlySummary,
                typeSummary,
                dailySummary,
                accountSummary
        );
    }

    /**
     * Generate monthly summary
     */
    private ReportDto.MonthlySummary generateMonthlySummary(List<Transaction> transactions) {
        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.DEPOSIT || 
                           t.getTransactionType() == TransactionType.TRANSFER_IN)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.WITHDRAWAL || 
                           t.getTransactionType() == TransactionType.TRANSFER_OUT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netAmount = totalIncome.subtract(totalExpense);
        int transactionCount = transactions.size();

        return new ReportDto.MonthlySummary(totalIncome, totalExpense, netAmount, transactionCount);
    }

    /**
     * Generate transaction type summary
     */
    private List<ReportDto.TransactionTypeSummary> generateTransactionTypeSummary(List<Transaction> transactions) {
        Map<TransactionType, List<Transaction>> grouped = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getTransactionType));

        return grouped.entrySet().stream()
                .map(entry -> {
                    BigDecimal amount = entry.getValue().stream()
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new ReportDto.TransactionTypeSummary(
                            entry.getKey().getCode(),
                            amount,
                            entry.getValue().size()
                    );
                })
                .sorted(Comparator.comparing(ReportDto.TransactionTypeSummary::amount).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Generate daily transaction summary for the last 30 days
     */
    private List<ReportDto.DailyTransactionSummary> generateDailyTransactionSummary(List<Transaction> transactions) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
        
        // Group by date
        Map<String, List<Transaction>> groupedByDate = transactions.stream()
                .collect(Collectors.groupingBy(t -> 
                    t.getCreatedAt().format(formatter)
                ));

        // Create summary for each date
        return groupedByDate.entrySet().stream()
                .map(entry -> {
                    BigDecimal income = entry.getValue().stream()
                            .filter(t -> t.getTransactionType() == TransactionType.DEPOSIT || 
                                       t.getTransactionType() == TransactionType.TRANSFER_IN)
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal expense = entry.getValue().stream()
                            .filter(t -> t.getTransactionType() == TransactionType.WITHDRAWAL || 
                                       t.getTransactionType() == TransactionType.TRANSFER_OUT)
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new ReportDto.DailyTransactionSummary(entry.getKey(), income, expense);
                })
                .sorted(Comparator.comparing(ReportDto.DailyTransactionSummary::date))
                .collect(Collectors.toList());
    }

    /**
     * Generate account balance summary
     */
    private List<ReportDto.AccountBalanceSummary> generateAccountBalanceSummary(List<Account> accounts) {
        return accounts.stream()
                .filter(Account::getIsActive)
                .map(account -> new ReportDto.AccountBalanceSummary(
                        account.getBankName() + " - " + account.getAccountNumber(),
                        account.getBalance(),
                        account.getCurrency()
                ))
                .sorted(Comparator.comparing(ReportDto.AccountBalanceSummary::balance).reversed())
                .collect(Collectors.toList());
    }
}
