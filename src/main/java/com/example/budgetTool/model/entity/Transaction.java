package com.example.budgetTool.model.entity;

import com.example.budgetTool.model.enums.TransactionStatus;
import com.example.budgetTool.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(length = 255)
    private String description;

    @Column(name = "reference_number", nullable = false, unique = true, length = 100)
    private String referenceNumber;

    @Column(name = "to_account_number", length = 50)
    private String toAccountNumber;

    @Column(name = "to_account_holder_name", length = 200)
    private String toAccountHolderName;

    @Column(name = "from_account_number", length = 50)
    private String fromAccountNumber;

    @Column(name = "from_account_holder_name", length = 200)
    private String fromAccountHolderName;

    @Column(name = "transfer_message", columnDefinition = "TEXT")
    private String transferMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(name = "balance_after", precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    private Transaction(Account account, TransactionType transactionType, BigDecimal amount, 
                       String currency, String description, String referenceNumber) {
        this.account = account;
        this.transactionType = transactionType;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.referenceNumber = referenceNumber;
    }

    public static Transaction of(Account account, TransactionType transactionType, BigDecimal amount, 
                                String currency, String description, String referenceNumber) {
        return new Transaction(account, transactionType, amount, currency, description, referenceNumber);
    }

    public void setTransferDetails(String toAccountNumber, String toAccountHolderName, String transferMessage) {
        this.toAccountNumber = toAccountNumber;
        this.toAccountHolderName = toAccountHolderName;
        this.transferMessage = transferMessage;
    }

    public void setFromDetails(String fromAccountNumber, String fromAccountHolderName) {
        this.fromAccountNumber = fromAccountNumber;
        this.fromAccountHolderName = fromAccountHolderName;
    }

    public void markAsCompleted(BigDecimal balanceAfter) {
        this.status = TransactionStatus.COMPLETED;
        this.balanceAfter = balanceAfter;
        this.processedAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = TransactionStatus.FAILED;
        this.processedAt = LocalDateTime.now();
    }

    public void markAsCancelled() {
        this.status = TransactionStatus.CANCELLED;
        this.processedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Transaction transaction)) return false;
        return Objects.equals(id, transaction.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}