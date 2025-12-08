package com.example.budgetTool.model.dto;

import com.example.budgetTool.model.entity.Transaction;
import com.example.budgetTool.model.enums.TransactionStatus;
import com.example.budgetTool.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * packageName    : com.example.budgetTool.model.dto
 * author         : kimjaehyeong
 * date           : 12/07/25
 * description    : Transaction DTO
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/07/25        kimjaehyeong       created
 */
public record TransactionDto(
        Long id,
        Long accountId,
        String accountNumber,
        String bankName,
        TransactionType transactionType,
        BigDecimal amount,
        String currency,
        String description,
        String referenceNumber,
        String toAccountNumber,
        String toAccountHolderName,
        String fromAccountNumber,
        String fromAccountHolderName,
        String transferMessage,
        TransactionStatus status,
        BigDecimal balanceAfter,
        LocalDateTime createdAt,
        LocalDateTime processedAt
) {
    public static TransactionDto from(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getAccount().getId(),
                transaction.getAccount().getAccountNumber(),
                transaction.getAccount().getBankName(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getDescription(),
                transaction.getReferenceNumber(),
                transaction.getToAccountNumber(),
                transaction.getToAccountHolderName(),
                transaction.getFromAccountNumber(),
                transaction.getFromAccountHolderName(),
                transaction.getTransferMessage(),
                transaction.getStatus(),
                transaction.getBalanceAfter(),
                transaction.getCreatedAt(),
                transaction.getProcessedAt()
        );
    }

    public record Response(
            Long id,
            Long accountId,
            String accountNumber,
            String bankName,
            String transactionType,
            BigDecimal amount,
            String currency,
            String description,
            String referenceNumber,
            String toAccountNumber,
            String toAccountHolderName,
            String fromAccountNumber,
            String fromAccountHolderName,
            String transferMessage,
            String status,
            BigDecimal balanceAfter,
            LocalDateTime createdAt,
            LocalDateTime processedAt
    ) {
        public static Response from(TransactionDto dto) {
            return new Response(
                    dto.id(),
                    dto.accountId(),
                    dto.accountNumber(),
                    dto.bankName(),
                    dto.transactionType().getCode(),
                    dto.amount(),
                    dto.currency(),
                    dto.description(),
                    dto.referenceNumber(),
                    dto.toAccountNumber(),
                    dto.toAccountHolderName(),
                    dto.fromAccountNumber(),
                    dto.fromAccountHolderName(),
                    dto.transferMessage(),
                    dto.status().getCode(),
                    dto.balanceAfter(),
                    dto.createdAt(),
                    dto.processedAt()
            );
        }
    }

    /**
     * Deposit Request DTO
     */
    public record DepositRequest(
            Long accountId,
            BigDecimal amount,
            String description
    ) {}

    /**
     * Withdrawal Request DTO
     */
    public record WithdrawalRequest(
            Long accountId,
            BigDecimal amount,

            String description
    ) {}

    /**
     * Transfer Request DTO
     */
    public record TransferRequest(
            Long fromAccountId,
            String toAccountNumber,
            String toAccountHolderName,
            BigDecimal amount,

            String transferMessage
    ) {}
}
