package com.example.budgetTool.service;

import com.example.budgetTool.model.entity.Account;
import com.example.budgetTool.model.entity.Transaction;
import com.example.budgetTool.model.enums.TransactionStatus;
import com.example.budgetTool.model.enums.TransactionType;
import com.example.budgetTool.repository.TransactionRepository;
import com.example.budgetTool.utils.querydsl.FieldCondition;
import com.example.budgetTool.utils.querydsl.LogicType;
import com.example.budgetTool.utils.querydsl.Operator;
import com.example.budgetTool.utils.querydsl.SortCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.example.budgetTool.service
 * author         : kimjaehyeong
 * date           : 12/07/25
 * description    : Transaction Service
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/07/25        kimjaehyeong       created
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    /**
     * Check if transaction exists with given conditions
     * @param fconds Field conditions
     * @return true if exists, false otherwise
     */
    public boolean exist(List<FieldCondition> fconds) {
        return this.transactionRepository.exist(fconds);
    }

    /**
     * Get a single transaction matched with conditions
     * @param fconds Field conditions
     * @param sconds Sort conditions
     * @return Transaction entity
     */
    public Transaction getTransaction(List<FieldCondition> fconds, List<SortCondition> sconds) {
        if (fconds == null && sconds == null) {
            return null;
        }
        return this.transactionRepository.getTransaction(fconds, sconds);
    }

    /**
     * Get transaction by ID
     * @param transactionId Transaction ID
     * @return Transaction entity
     */
    public Transaction getTransactionById(Long transactionId) {
        return this.transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + transactionId));
    }

    /**
     * Get list of transactions matched with conditions
     * @param fconds Field conditions
     * @param sconds Sort conditions
     * @return List of Transaction entities
     */
    public List<Transaction> getTransactionList(List<FieldCondition> fconds, List<SortCondition> sconds) {
        return this.transactionRepository.getTransactionList(fconds, sconds);
    }

    /**
     * Get all transactions for a specific user
     * @param userId User ID
     * @return List of Transaction entities
     */
    public List<Transaction> getTransactionListByUserId(Long userId) {
        return this.transactionRepository.findByUserId(userId);
    }

    /**
     * Get transactions by account ID
     * @param accountId Account ID
     * @return List of Transaction entities
     */
    public List<Transaction> getTransactionListByAccountId(Long accountId) {
        return this.transactionRepository.findByAccountId(accountId);
    }

    /**
     * Get transactions by date range for a user
     * @param userId User ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of Transaction entities
     */
    public List<Transaction> getTransactionListByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return this.transactionRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    /**
     * Add a new transaction
     * @param transaction Transaction entity
     * @return Saved Transaction entity
     */
    public Transaction addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        
        return this.transactionRepository.save(transaction);
    }

    /**
     * Update an existing transaction
     * @param transaction Transaction entity
     * @return Updated Transaction entity
     */
    public Transaction updateTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        
        if (transaction.getId() == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null for update");
        }
        
        if (!this.transactionRepository.existsById(transaction.getId())) {
            throw new IllegalArgumentException("Transaction not found with id: " + transaction.getId());
        }
        
        return this.transactionRepository.save(transaction);
    }

    /**
     * Delete a transaction by ID
     * @param transactionId Transaction ID
     */
    public void deleteTransaction(Long transactionId) {
        if (!this.transactionRepository.existsById(transactionId)) {
            throw new IllegalArgumentException("Transaction not found with id: " + transactionId);
        }
        
        this.transactionRepository.deleteById(transactionId);
    }

    /**
     * Process Deposit Transaction
     * @param account Account to deposit into
     * @param amount Amount to deposit
     * @param description Transaction description
     * @return Created Transaction entity
     */
    public Transaction processDeposit(Account account, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        // Update account balance
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountService.updateAccount(account);

        // Create transaction record
        String referenceNumber = generateReferenceNumber("DEP");
        Transaction transaction = Transaction.of(
                account,
                TransactionType.DEPOSIT,
                amount,
                account.getCurrency(),
                description != null ? description : "Deposit",
                referenceNumber
        );
        
        transaction.markAsCompleted(newBalance);
        
        return this.transactionRepository.save(transaction);
    }

    /**
     * Process Withdrawal Transaction
     * @param account Account to withdraw from
     * @param amount Amount to withdraw
     * @param description Transaction description
     * @return Created Transaction entity
     */
    public Transaction processWithdrawal(Account account, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        // Check sufficient balance
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        // Update account balance
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountService.updateAccount(account);

        // Create transaction record
        String referenceNumber = generateReferenceNumber("WTH");
        Transaction transaction = Transaction.of(
                account,
                TransactionType.WITHDRAWAL,
                amount,
                account.getCurrency(),
                description != null ? description : "Withdrawal",
                referenceNumber
        );
        
        transaction.markAsCompleted(newBalance);
        
        return this.transactionRepository.save(transaction);
    }

    /**
     * Process Transfer Transaction
     * @param fromAccount Account to transfer from
     * @param toAccountNumber Recipient account number
     * @param toAccountHolderName Recipient account holder name
     * @param amount Amount to transfer
     * @param transferMessage Transfer message
     * @return Created Transaction entity
     */
    public Transaction processTransfer(Account fromAccount, String toAccountNumber,
                                      String toAccountHolderName, BigDecimal amount, 
                                      String transferMessage) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        // Check sufficient balance
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        // Update account balance
        BigDecimal newBalance = fromAccount.getBalance().subtract(amount);
        fromAccount.setBalance(newBalance);
        accountService.updateAccount(fromAccount);

        //update target account balance
        List<FieldCondition> fconds = new ArrayList<>();
        fconds.add(new FieldCondition("user.id",  Operator.EQ, fromAccount.getUser().getId(), LogicType.AND));
        fconds.add(new FieldCondition("accountNumber",  Operator.EQ, toAccountNumber, LogicType.AND));
        Account toAccount = this.accountService.getAccount(fconds, null);
        if (toAccount == null)
            throw new NullPointerException("To Account not found");
        toAccount.setBalance(toAccount.getBalance().add(amount));
        this.accountService.updateAccount(toAccount);

        // Create transaction record
        String referenceNumber = generateReferenceNumber("TRF");
        Transaction transaction = Transaction.of(
                fromAccount,
                TransactionType.TRANSFER_OUT,
                amount,
                fromAccount.getCurrency(),
                "Transfer to " + toAccountHolderName,
                referenceNumber
        );
        
        transaction.setTransferDetails(toAccountNumber, toAccountHolderName, transferMessage);
        transaction.markAsCompleted(newBalance);
        
        return this.transactionRepository.save(transaction);
    }

    /**
     * Generate unique reference number for transaction
     * @param prefix Transaction type prefix (DEP, WTH, TRF)
     * @return Unique reference number
     */
    private String generateReferenceNumber(String prefix) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + "-" + timestamp + "-" + uuid;
    }
}
