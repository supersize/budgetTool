package com.example.budgetTool.service;

import com.example.budgetTool.model.dto.DashboardDto;
import com.example.budgetTool.model.entity.Account;
import com.example.budgetTool.model.entity.Transaction;
import com.example.budgetTool.model.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName    : com.example.budgetTool.service
 * author         : kimjaehyeong
 * date           : 12/08/25
 * description    : Dashboard Service
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/08/25        kimjaehyeong       created
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardService {

    private final AccountService accountService;
    private final TransactionService transactionService;

    /**
     * Get dashboard summary for user
     * @param userId User ID
     * @return Dashboard summary
     */
    public DashboardDto.DashboardSummary getDashboardSummary(Long userId) {
        // Get all accounts
        List<Account> accounts = accountService.getAccountListByUserId(userId);
        List<Account> activeAccounts = accounts.stream()
                .filter(Account::getIsActive)
                .collect(Collectors.toList());

        // Calculate total balance
        BigDecimal totalBalance = activeAccounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Get current month transactions
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime now = LocalDateTime.now();
        List<Transaction> monthTransactions = transactionService.getTransactionListByDateRange(userId, startOfMonth, now);

        // Calculate monthly growth
        BigDecimal monthlyIncome = monthTransactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.DEPOSIT || 
                           t.getTransactionType() == TransactionType.TRANSFER_IN)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthlyExpense = monthTransactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.WITHDRAWAL || 
                           t.getTransactionType() == TransactionType.TRANSFER_OUT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthlyGrowth = monthlyIncome.subtract(monthlyExpense);
        
        // Calculate growth percentage
        double growthPercentage = 0.0;
        if (totalBalance.compareTo(BigDecimal.ZERO) > 0) {
            growthPercentage = monthlyGrowth
                    .divide(totalBalance.subtract(monthlyGrowth), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        // Get account overviews
        List<DashboardDto.AccountOverview> accountOverviews = generateAccountOverviews(activeAccounts, totalBalance);

        // Get recent transactions (last 5)
        List<Transaction> allTransactions = transactionService.getTransactionListByUserId(userId);
        List<DashboardDto.RecentTransaction> recentTransactions = allTransactions.stream()
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .limit(5)
                .map(this::mapToRecentTransaction)
                .collect(Collectors.toList());

        return new DashboardDto.DashboardSummary(
                activeAccounts.size(),
                totalBalance,
                monthlyGrowth,
                growthPercentage,
                accountOverviews,
                recentTransactions
        );
    }

    /**
     * Generate account overviews with percentage
     */
    private List<DashboardDto.AccountOverview> generateAccountOverviews(List<Account> accounts, BigDecimal totalBalance) {
        return accounts.stream()
                .map(account -> {
                    double percentage = 0.0;
                    if (totalBalance.compareTo(BigDecimal.ZERO) > 0) {
                        percentage = account.getBalance()
                                .divide(totalBalance, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100))
                                .doubleValue();
                    }
                    return new DashboardDto.AccountOverview(
                            account.getId(),
                            account.getBankName(),
                            account.getAccountNumber(),
                            account.getBalance(),
                            account.getCurrency(),
                            percentage
                    );
                })
                .sorted(Comparator.comparing(DashboardDto.AccountOverview::balance).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Map Transaction to RecentTransaction
     */
    private DashboardDto.RecentTransaction mapToRecentTransaction(Transaction transaction) {
        String title = generateTransactionTitle(transaction);
        String accountInfo = transaction.getAccount().getBankName() + " " + 
                           maskAccountNumber(transaction.getAccount().getAccountNumber());
        boolean isIncome = transaction.getTransactionType() == TransactionType.DEPOSIT || 
                          transaction.getTransactionType() == TransactionType.TRANSFER_IN;

        return new DashboardDto.RecentTransaction(
                transaction.getId(),
                transaction.getTransactionType().getCode(),
                title,
                accountInfo,
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getCreatedAt(),
                isIncome
        );
    }

    /**
     * Generate transaction title
     */
    private String generateTransactionTitle(Transaction transaction) {
        if (transaction.getDescription() != null && !transaction.getDescription().isEmpty()) {
            return transaction.getDescription();
        }

        switch (transaction.getTransactionType()) {
            case DEPOSIT:
                return "Deposit";
            case WITHDRAWAL:
                return "Withdrawal";
            case TRANSFER_OUT:
                return "Transfer to " + (transaction.getToAccountHolderName() != null ? 
                       transaction.getToAccountHolderName() : "Account");
            case TRANSFER_IN:
                return "Transfer from " + (transaction.getFromAccountHolderName() != null ? 
                       transaction.getFromAccountHolderName() : "Account");
            default:
                return "Transaction";
        }
    }

    /**
     * Mask account number
     */
    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return accountNumber;
        }
        return "***" + accountNumber.substring(accountNumber.length() - 4);
    }
}
