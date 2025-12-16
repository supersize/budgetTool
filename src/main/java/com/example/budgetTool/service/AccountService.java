package com.example.budgetTool.service;

import com.example.budgetTool.model.entity.Account;
import com.example.budgetTool.model.entity.User;
import com.example.budgetTool.repository.AccountRepository;
import com.example.budgetTool.utils.querydsl.FieldCondition;
import com.example.budgetTool.utils.querydsl.LogicType;
import com.example.budgetTool.utils.querydsl.Operator;
import com.example.budgetTool.utils.querydsl.SortCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * packageName    : com.example.budgetTool.service
 * author         : kimjaehyeong
 * date           : 12/05/25
 * description    : Account Service
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/05/25        kimjaehyeong       created
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {
    
    private final AccountRepository accountRepository;

    /**
     * Check if account exists with given conditions
     * @param fconds Field conditions
     * @return true if exists, false otherwise
     */
    public boolean exist(List<FieldCondition> fconds) {
        return this.accountRepository.exist(fconds);
    }

    /**
     * Get a single account matched with conditions
     * @param fconds Field conditions
     * @param sconds Sort conditions
     * @return Account entity
     */
    public Account getAccount(List<FieldCondition> fconds, List<SortCondition> sconds) {
        if (fconds == null && sconds == null) {
            return null;
        }
        return this.accountRepository.getAccount(fconds, sconds);
    }

    /**
     * Get account by ID
     * @param accountId Account ID
     * @return Account entity
     */
    public Account getAccountById(Long accountId) {
        return this.accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));
    }

    /**
     * Get list of accounts matched with conditions
     * @param fconds Field conditions
     * @param sconds Sort conditions
     * @return List of Account entities
     */
    public List<Account> getAccountList(List<FieldCondition> fconds, List<SortCondition> sconds) {
        return this.accountRepository.getAccountList(fconds, sconds);
    }

    /**
     * Get all accounts for a specific user
     * @param userId User ID
     * @return List of Account entities
     */
    public List<Account> getAccountListByUserId(Long userId) {
        List<FieldCondition> fconds = List.of(
                new FieldCondition("user.id",
                        Operator.EQ,
                        userId, 
                        LogicType.AND)
        );
        List<SortCondition> sconds = List.of(
                new SortCondition("createdAt", SortCondition.SortDirection.DESC)
        );
        return this.accountRepository.getAccountList(fconds, sconds);
    }

    /**
     * Add a new account
     * @param account Account entity
     * @return Saved Account entity
     */
    public Account addAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        
        // Check if account already exists
        List<FieldCondition> fconds = List.of(
                new FieldCondition("accountNumber", 
                        Operator.EQ,
                        account.getAccountNumber(), 
                        LogicType.AND)
                , new FieldCondition("bankName",
                        Operator.EQ,
                        account.getBankName(),
                        LogicType.AND)
        );

        if (this.accountRepository.exist(fconds)) {
            throw new IllegalArgumentException("Account number already exists: " + account.getAccountNumber());
        }
        
        return this.accountRepository.save(account);
    }

    /**
     * Update an existing account
     * @param account Account entity
     * @return Updated Account entity
     */
    public Account updateAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        
        if (account.getId() == null) {
            throw new IllegalArgumentException("Account ID cannot be null for update");
        }
        
        // Check if account exists
        if (!this.accountRepository.existsById(account.getId())) {
            throw new IllegalArgumentException("Account not found with id: " + account.getId());
        }
        
        return this.accountRepository.save(account);
    }

    /**
     * Delete an account by ID
     * @param accountId Account ID
     */
    public void deleteAccount(Long accountId) {
        if (!this.accountRepository.existsById(accountId)) {
            throw new IllegalArgumentException("Account not found with id: " + accountId);
        }
        
        this.accountRepository.deleteById(accountId);
    }

    /**
     * Update account balance
     * @param accountId Account ID
     * @param amount Amount to add (positive) or subtract (negative)
     * @return Updated Account entity
     */
    public Account updateBalance(Long accountId, BigDecimal amount) {
        Account account = this.getAccountById(accountId);
        account.updateBalance(amount);
        return this.accountRepository.save(account);
    }

    /**
     * Check if account has sufficient funds
     * @param accountId Account ID
     * @param amount Amount to check
     * @return true if sufficient, false otherwise
     */
    public boolean hasSufficientFunds(Long accountId, BigDecimal amount) {
        Account account = this.getAccountById(accountId);
        return account.hasSufficientFunds(amount);
    }
}
