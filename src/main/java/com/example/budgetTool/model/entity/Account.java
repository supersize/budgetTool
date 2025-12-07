package com.example.budgetTool.model.entity;

import com.example.budgetTool.model.enums.AccountType;
import com.example.budgetTool.model.enums.converter.AccountTypeConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "accounts")
@EntityListeners(AuditingEntityListener.class)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "account_number", nullable = false, unique = true, length = 50)
    private String accountNumber;

    @Convert(converter = AccountTypeConverter.class)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType = AccountType.SAVINGS;

    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    @Column(nullable = false, length = 3)
    private String currency = "GBP";

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    private Account(User user, String accountNumber, AccountType accountType, String bankName, String currency) {
        this.user = user;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.bankName = bankName;
        this.currency = currency;
    }

    public static Account of(User user, String accountNumber, AccountType accountType, String bankName, String currency) {
        return new Account(user, accountNumber, accountType, bankName, currency);
    }

    public void updateBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public boolean hasSufficientFunds(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Account account)) return false;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
