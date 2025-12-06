package com.example.budgetTool.controller.rest;

import com.example.budgetTool.model.dto.AccountDto;
import com.example.budgetTool.model.dto.ApiResponse;
import com.example.budgetTool.model.entity.Account;
import com.example.budgetTool.model.entity.User;
import com.example.budgetTool.model.enums.AccountType;
import com.example.budgetTool.service.AccountService;
import com.example.budgetTool.service.UserService;
import com.example.budgetTool.utils.querydsl.FieldCondition;
import com.example.budgetTool.utils.querydsl.LogicType;
import com.example.budgetTool.utils.querydsl.Operator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName    : com.example.budgetTool.controller.rest
 * author         : kimjaehyeong
 * date           : 12/05/25
 * description    : Account REST API Controller
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/05/25        kimjaehyeong       created
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/Accounts")
public class AccountRestController {

    private final AccountService accountService;
    private final UserService userService;

    /**
     * Get current authenticated user
     * @return User entity
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        List<FieldCondition> fconds = new ArrayList<>();
        fconds.add(new FieldCondition("email", Operator.EQ, email, LogicType.AND));

        return this.userService.getUser(fconds, null);
    }

    /**
     * Get all accounts for current user
     * @return API response with list of accounts
     */
    @GetMapping
    public ApiResponse<List<AccountDto.Response>> getAccounts() {
        ApiResponse<List<AccountDto.Response>> res = new ApiResponse<>();
        try {
            User currentUser = getCurrentUser();

            List<Account> accounts = this.accountService.getAccountListByUserId(currentUser.getId());

            List<AccountDto.Response> responseList = accounts.stream()
                    .map(AccountDto::from)
                    .map(AccountDto.Response::from)
                    .collect(Collectors.toList());

            res = ApiResponse.SUCCESS(responseList);
        } catch (Exception e) {
            log.error("An error occurs when getting accounts:", e);
            res = ApiResponse.ERROR("[error] Failed to get accounts: " + e.getMessage());
        }

        return res;
    }

    /**
     * Get account by ID
     * @param accountId Account ID
     * @return API response with account details
     */
    @GetMapping("/{accountId}")
    public ApiResponse<AccountDto.Response> getAccount(@PathVariable Long accountId) {
        ApiResponse<AccountDto.Response> res = new ApiResponse<>();
        try {
            User currentUser = getCurrentUser();

            Account account = this.accountService.getAccountById(accountId);

            // Check if account belongs to current user
            if (!account.getUser().getId().equals(currentUser.getId())) {
                return ApiResponse.ERROR("You don't have permission to access this account");
            }

            AccountDto.Response response = AccountDto.Response.from(AccountDto.from(account));
            res = ApiResponse.SUCCESS(response);
        } catch (IllegalArgumentException e) {
            log.error("Account not found:", e);
            res = ApiResponse.ERROR(e.getMessage());
        } catch (Exception e) {
            log.error("An error occurs when getting account:", e);
            res = ApiResponse.ERROR("[error] Failed to get account: " + e.getMessage());
        }

        return res;
    }

    /**
     * Create new account
     * @param request Account creation request
     * @return API response with created account
     */
    @PostMapping
    public ApiResponse<AccountDto.Response> createAccount(@RequestBody CreateAccountRequest request) {
        ApiResponse<AccountDto.Response> res = new ApiResponse<>();
        try {
            User currentUser = getCurrentUser();

            // Validate request
            if (request.bankName == null || request.bankName.trim().isEmpty()) {
                return ApiResponse.ERROR("Bank name is required");
            }
            if (request.accountNumber == null || request.accountNumber.trim().isEmpty()) {
                return ApiResponse.ERROR("Account number is required");
            }
            if (request.accountType == null) {
                return ApiResponse.ERROR("Account type is required");
            }
            if (request.currency == null || request.currency.trim().isEmpty()) {
                return ApiResponse.ERROR("Currency is required");
            }

            // Create account entity
            Account account = Account.of(
                    currentUser,
                    request.accountNumber,
                    request.accountType,
                    request.bankName,
                    request.currency
            );

            // Set initial balance if provided
            if (request.initialBalance != null && request.initialBalance.compareTo(BigDecimal.ZERO) > 0) {
                account.setBalance(request.initialBalance);
            }

            Account savedAccount = this.accountService.addAccount(account);

            AccountDto.Response response = AccountDto.Response.from(AccountDto.from(savedAccount));
            res = ApiResponse.SUCCESS(response);

        } catch (IllegalArgumentException e) {
            log.error("Validation error:", e);
            res = ApiResponse.ERROR(e.getMessage());
        } catch (Exception e) {
            log.error("An error occurs when creating account:", e);
            res = ApiResponse.ERROR("[error] Failed to create account: " + e.getMessage());
        }

        return res;
    }

    /**
     * Update account
     * @param accountId Account ID
     * @param request Account update request
     * @return API response with updated account
     */
    @PutMapping("/{accountId}")
    public ApiResponse<AccountDto.Response> updateAccount(
            @PathVariable Long accountId,
            @RequestBody UpdateAccountRequest request) {
        ApiResponse<AccountDto.Response> res = new ApiResponse<>();
        try {
            User currentUser = getCurrentUser();

            Account account = this.accountService.getAccountById(accountId);

            // Check if account belongs to current user
            if (!account.getUser().getId().equals(currentUser.getId())) {
                return ApiResponse.ERROR("You don't have permission to update this account");
            }

            // Update account fields
            if (request.bankName != null && !request.bankName.trim().isEmpty()) {
                account.setBankName(request.bankName);
            }
            if (request.accountNumber != null && !request.accountNumber.trim().isEmpty()) {
                account.setAccountNumber(request.accountNumber);
            }
            if (request.accountType != null) {
                account.setAccountType(request.accountType);
            }
            if (request.currency != null && !request.currency.trim().isEmpty()) {
                account.setCurrency(request.currency);
            }
            if (request.isActive != null) {
                account.setIsActive(request.isActive);
            }

            Account updatedAccount = this.accountService.updateAccount(account);

            AccountDto.Response response = AccountDto.Response.from(AccountDto.from(updatedAccount));
            res = ApiResponse.SUCCESS(response);

        } catch (IllegalArgumentException e) {
            log.error("Validation error:", e);
            res = ApiResponse.ERROR(e.getMessage());
        } catch (Exception e) {
            log.error("An error occurs when updating account:", e);
            res = ApiResponse.ERROR("[error] Failed to update account: " + e.getMessage());
        }

        return res;
    }

    /**
     * Delete account
     * @param accountId Account ID
     * @return API response
     */
    @DeleteMapping("/{accountId}")
    public ApiResponse<String> deleteAccount(@PathVariable Long accountId) {
        ApiResponse<String> res = new ApiResponse<>();
        try {
            User currentUser = getCurrentUser();

            Account account = this.accountService.getAccountById(accountId);

            // Check if account belongs to current user
            if (!account.getUser().getId().equals(currentUser.getId())) {
                return ApiResponse.ERROR("You don't have permission to delete this account");
            }

            this.accountService.deleteAccount(accountId);

            res = ApiResponse.SUCCESS("Account deleted successfully");

        } catch (IllegalArgumentException e) {
            log.error("Validation error:", e);
            res = ApiResponse.ERROR(e.getMessage());
        } catch (Exception e) {
            log.error("An error occurs when deleting account:", e);
            res = ApiResponse.ERROR("[error] Failed to delete account: " + e.getMessage());
        }

        return res;
    }

    /**
     * Request DTO for creating account
     */
    public record CreateAccountRequest(
            String bankName,
            String accountNumber,
            AccountType accountType,
            String currency,
            BigDecimal initialBalance
    ) {}

    /**
     * Request DTO for updating account
     */
    public record UpdateAccountRequest(
            String bankName,
            String accountNumber,
            AccountType accountType,
            String currency,
            Boolean isActive
    ) {}
}
