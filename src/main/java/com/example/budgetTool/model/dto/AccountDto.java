package com.example.budgetTool.model.dto;

import com.example.budgetTool.model.entity.Account;
import com.example.budgetTool.model.entity.User;
import com.example.budgetTool.model.enums.AccountType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountDto(
        Long id,
        UserDto user,
        String accountNumber,
        AccountType accountType,
        String bankName,
        String currency,
        BigDecimal balance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean isActive
) {

    @Builder
    public AccountDto {}

    public static AccountDto from(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .user(UserDto.from(account.getUser()))
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .bankName(account.getBankName())
                .currency(account.getCurrency())
                .balance(account.getBalance())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .isActive(account.getIsActive())
                .build();
    }

    public record Request(
            String accountNumber,
            AccountType accountType,
            String bankName,
            String currency
    ) {
        public Account toEntity(User user) {
            return Account.of(user, accountNumber, accountType, bankName, currency);
        }
    }

    public record Response(
            Long id,
            String accountNumber,
            AccountType accountType,
            String bankName,
            String currency,
            BigDecimal balance,
            Boolean isActive,
            LocalDateTime createdAt
    ) {
        public static Response from(AccountDto dto) {
            return new Response(
                    dto.id(),
                    dto.accountNumber(),
                    dto.accountType(),
                    dto.bankName(),
                    dto.currency(),
                    dto.balance(),
                    dto.isActive(),
                    dto.createdAt()
            );
        }
    }

    public record Summary(
            Long id,
            String accountNumber,
            AccountType accountType,
            String bankName,
            BigDecimal balance,
            String currency
    ) {
        public static Summary from(AccountDto dto) {
            return new Summary(
                    dto.id(),
                    dto.accountNumber(),
                    dto.accountType(),
                    dto.bankName(),
                    dto.balance(),
                    dto.currency()
            );
        }
    }
}