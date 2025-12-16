package com.example.budgetTool.controller.rest;

import com.example.budgetTool.model.dto.ApiResponse;
import com.example.budgetTool.model.dto.TransactionDto;
import com.example.budgetTool.model.entity.Account;
import com.example.budgetTool.model.entity.Transaction;
import com.example.budgetTool.model.entity.User;
import com.example.budgetTool.service.AccountService;
import com.example.budgetTool.service.TransactionService;
import com.example.budgetTool.utils.querydsl.FieldCondition;
import com.example.budgetTool.utils.querydsl.LogicType;
import com.example.budgetTool.utils.querydsl.Operator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName    : com.example.budgetTool.controller.rest
 * author         : kimjaehyeong
 * date           : 12/07/25
 * description    : Transaction REST API Controller
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/07/25        kimjaehyeong       created
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/Transactions")
public class TransactionRestController {

    private final TransactionService transactionService;
    private final AccountService accountService;

    /**
     * Get all transactions for current user
     * @return API response with list of transactions
     */
    @GetMapping
    public ApiResponse<List<TransactionDto.Response>> getTransactions(@AuthenticationPrincipal User currentUser) {
        ApiResponse<List<TransactionDto.Response>> res = new ApiResponse<>();
        try {
            List<Transaction> transactions = this.transactionService.getTransactionListByUserId(currentUser.getId());

            List<TransactionDto.Response> responseList = transactions.stream()
                    .map(TransactionDto::from)
                    .map(TransactionDto.Response::from)
                    .collect(Collectors.toList());

            res = ApiResponse.SUCCESS(responseList);
        } catch (Exception e) {
            log.error("An error occurs when getting transactions:", e);
            res = ApiResponse.ERROR("[error] Failed to get transactions: " + e.getMessage());
        }

        return res;
    }

    /**
     * Get transaction by ID
     * @param transactionId Transaction ID
     * @return API response with transaction details
     */
    @GetMapping("/{transactionId}")
    public ApiResponse<TransactionDto.Response> getTransaction(@PathVariable Long transactionId, @AuthenticationPrincipal User currentUser) {
        ApiResponse<TransactionDto.Response> res = new ApiResponse<>();
        try {
            Transaction transaction = this.transactionService.getTransactionById(transactionId);

            // Check if transaction belongs to current user's account
            if (!transaction.getAccount().getUser().getId().equals(currentUser.getId())) {
                return ApiResponse.ERROR("You don't have permission to access this transaction");
            }

            TransactionDto.Response response = TransactionDto.Response.from(TransactionDto.from(transaction));
            res = ApiResponse.SUCCESS(response);
        } catch (IllegalArgumentException e) {
            log.error("Transaction not found:", e);
            res = ApiResponse.ERROR(e.getMessage());
        } catch (Exception e) {
            log.error("An error occurs when getting transaction:", e);
            res = ApiResponse.ERROR("[error] Failed to get transaction: " + e.getMessage());
        }

        return res;
    }

    /**
     * Get transactions by account ID
     * @param accountId Account ID
     * @return API response with list of transactions
     */
    @GetMapping("/account/{accountId}")
    public ApiResponse<List<TransactionDto.Response>> getTransactionsByAccount(
            @PathVariable Long accountId, @AuthenticationPrincipal User currentUser) {
        ApiResponse<List<TransactionDto.Response>> res = new ApiResponse<>();
        try {
            List<Transaction> transactions = this.transactionService.getTransactionListByAccountId(accountId);

            // Check if any transaction exists and belongs to current user
            if (!transactions.isEmpty() && 
                !transactions.get(0).getAccount().getUser().getId().equals(currentUser.getId())) {
                return ApiResponse.ERROR("You don't have permission to access these transactions");
            }

            List<TransactionDto.Response> responseList = transactions.stream()
                    .map(TransactionDto::from)
                    .map(TransactionDto.Response::from)
                    .collect(Collectors.toList());

            res = ApiResponse.SUCCESS(responseList);
        } catch (Exception e) {
            log.error("An error occurs when getting transactions by account:", e);
            res = ApiResponse.ERROR("[error] Failed to get transactions: " + e.getMessage());
        }

        return res;
    }

    /**
     * Get transactions by date range
     * @param startDate Start date (ISO format: 2024-01-01T00:00:00)
     * @param endDate End date (ISO format: 2024-12-31T23:59:59)
     * @return API response with list of transactions
     */
    @GetMapping("/date-range")
    public ApiResponse<List<TransactionDto.Response>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @AuthenticationPrincipal User currentUser) {
        ApiResponse<List<TransactionDto.Response>> res = new ApiResponse<>();
        try {
            List<Transaction> transactions = this.transactionService.getTransactionListByDateRange(
                    currentUser.getId(), startDate, endDate);

            List<TransactionDto.Response> responseList = transactions.stream()
                    .map(TransactionDto::from)
                    .map(TransactionDto.Response::from)
                    .collect(Collectors.toList());

            res = ApiResponse.SUCCESS(responseList);
        } catch (Exception e) {
            log.error("An error occurs when getting transactions by date range:", e);
            res = ApiResponse.ERROR("[error] Failed to get transactions: " + e.getMessage());
        }

        return res;
    }

    /**
     * Process Deposit Transaction
     * @param request Deposit request
     * @param currentUser Current authenticated user
     * @return API response with created transaction
     */
    @PostMapping("/deposit")
    public ApiResponse<TransactionDto.Response> deposit(
            @Validated @RequestBody TransactionDto.DepositRequest request,
            @AuthenticationPrincipal User currentUser) {
        ApiResponse<TransactionDto.Response> res = new ApiResponse<>();
        try {
            // Get account and verify ownership
            Account account = accountService.getAccountById(request.accountId());
            if (!account.getUser().getId().equals(currentUser.getId())) {
                return ApiResponse.ERROR("You don't have permission to access this account");
            }

            // Process deposit
            Transaction transaction = transactionService.processDeposit(
                    account, 
                    request.amount(),
                    request.description()
            );

            TransactionDto.Response response = TransactionDto.Response.from(TransactionDto.from(transaction));
            res = ApiResponse.SUCCESS(response);
            
            log.info("Deposit completed: {} deposited to account {}", request.amount(), account.getAccountNumber());
        } catch (IllegalArgumentException e) {
            log.error("Deposit validation error:", e);
            res = ApiResponse.ERROR(e.getMessage());
        } catch (Exception e) {
            log.error("An error occurs when processing deposit:", e);
            res = ApiResponse.ERROR("[error] Failed to process deposit: " + e.getMessage());
        }

        return res;
    }

    /**
     * Process Withdrawal Transaction
     * @param request Withdrawal request
     * @param currentUser Current authenticated user
     * @return API response with created transaction
     */
    @PostMapping("/withdraw")
    public ApiResponse<TransactionDto.Response> withdraw(
            @Validated @RequestBody TransactionDto.WithdrawalRequest request,
            @AuthenticationPrincipal User currentUser) {
        ApiResponse<TransactionDto.Response> res = new ApiResponse<>();
        try {
            // Get account and verify ownership
            Account account = accountService.getAccountById(request.accountId());
            if (!account.getUser().getId().equals(currentUser.getId())) {
                return ApiResponse.ERROR("You don't have permission to access this account");
            }

            // Process withdrawal
            Transaction transaction = transactionService.processWithdrawal(
                    account, 
                    request.amount(),
                    request.description()
            );

            TransactionDto.Response response = TransactionDto.Response.from(TransactionDto.from(transaction));
            res = ApiResponse.SUCCESS(response);
            
            log.info("Withdrawal completed: {} withdrawn from account {}", request.amount(), account.getAccountNumber());
        } catch (IllegalArgumentException e) {
            log.error("Withdrawal validation error:", e);
            res = ApiResponse.ERROR(e.getMessage());
        } catch (Exception e) {
            log.error("An error occurs when processing withdrawal:", e);
            res = ApiResponse.ERROR("[error] Failed to process withdrawal: " + e.getMessage());
        }

        return res;
    }

    /**
     * Process Transfer Transaction
     * @param request Transfer request
     * @param currentUser Current authenticated user
     * @return API response with created transaction
     */
    @PostMapping("/transfer")
    public ApiResponse<TransactionDto.Response> transfer(
            @Validated @RequestBody TransactionDto.TransferRequest request,
            @AuthenticationPrincipal User currentUser) {
        ApiResponse<TransactionDto.Response> res = new ApiResponse<>();
        try {
            // Get account and verify ownership
            Account fromAccount = accountService.getAccountById(request.fromAccountId());
            if (!fromAccount.getUser().getId().equals(currentUser.getId())) {
                return ApiResponse.ERROR("You don't have permission to access this account");
            }

            //confirm that target account is available
            List<FieldCondition> fcond = new ArrayList<>();
            fcond.add(new FieldCondition("accountNumber", Operator.EQ, request.toAccountNumber(), LogicType.AND));
            fcond.add(new FieldCondition("bankName", Operator.EQ, request.toBankName(), LogicType.AND));

            Account targetAccount = this.accountService.getAccount(fcond, null);
            if(targetAccount == null) { return ApiResponse.ERROR("can't be found the receiver's account. Please check it again."); }

            // Process transfer
            Transaction transaction = transactionService.processTransfer(
                    fromAccount,
                    request.toAccountNumber(),
                    request.toAccountHolderName(),
                    request.toBankName(),
                    request.amount(),
                    request.transferMessage()
            );

            TransactionDto.Response response = TransactionDto.Response.from(TransactionDto.from(transaction));
            res = ApiResponse.SUCCESS(response);
            
            log.info("Transfer completed: {} transferred from account {} to {}", 
                    request.amount(), fromAccount.getAccountNumber(), request.toAccountNumber());
        } catch (IllegalArgumentException e) {
            log.error("Transfer validation error:", e);
            res = ApiResponse.ERROR(e.getMessage());
        } catch (Exception e) {
            log.error("An error occurs when processing transfer:", e);
            res = ApiResponse.ERROR("[error] Failed to process transfer: " + e.getMessage());
        }

        return res;
    }
}
