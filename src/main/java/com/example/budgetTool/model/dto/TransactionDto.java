package com.example.budgetTool.model.dto;

import com.example.budgetTool.model.enums.TransactionStatus;
import com.example.budgetTool.model.enums.TransactionType;
import com.example.budgetTool.model.entity.Account;
import com.example.budgetTool.model.entity.Transaction;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDto(
        Long id,
        AccountDto account,
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

    @Builder
    public TransactionDto {}

    public static TransactionDto from(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .account(AccountDto.from(transaction.getAccount()))
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .description(transaction.getDescription())
                .referenceNumber(transaction.getReferenceNumber())
                .toAccountNumber(transaction.getToAccountNumber())
                .toAccountHolderName(transaction.getToAccountHolderName())
                .fromAccountNumber(transaction.getFromAccountNumber())
                .fromAccountHolderName(transaction.getFromAccountHolderName())
                .transferMessage(transaction.getTransferMessage())
                .status(transaction.getStatus())
                .balanceAfter(transaction.getBalanceAfter())
                .createdAt(transaction.getCreatedAt())
                .processedAt(transaction.getProcessedAt())
                .build();
    }

    public record Request(
            Long accountId,
            TransactionType transactionType,
            BigDecimal amount,
            String currency,
            String description,
            String toAccountNumber,
            String toAccountHolderName,
            String transferMessage
    ) {
        public Transaction toEntity(Account account, String referenceNumber) {
            return Transaction.of(account, transactionType, amount, currency, description, referenceNumber);
        }
    }

    public record Response(
            Long id,
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
        public static Response from(TransactionDto dto) {
            return new Response(
                    dto.id(),
                    dto.transactionType(),
                    dto.amount(),
                    dto.currency(),
                    dto.description(),
                    dto.referenceNumber(),
                    dto.toAccountNumber(),
                    dto.toAccountHolderName(),
                    dto.fromAccountNumber(),
                    dto.fromAccountHolderName(),
                    dto.transferMessage(),
                    dto.status(),
                    dto.balanceAfter(),
                    dto.createdAt(),
                    dto.processedAt()
            );
        }
    }

    public record Summary(
            Long id,
            TransactionType transactionType,
            BigDecimal amount,
            String currency,
            String description,
            TransactionStatus status,
            LocalDateTime createdAt
    ) {
        public static Summary from(TransactionDto dto) {
            return new Summary(
                    dto.id(),
                    dto.transactionType(),
                    dto.amount(),
                    dto.currency(),
                    dto.description(),
                    dto.status(),
                    dto.createdAt()
            );
        }
    }
}