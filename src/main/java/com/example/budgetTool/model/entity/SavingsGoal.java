package com.example.budgetTool.model.entity;

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
@Table(name = "savings_goals")
@EntityListeners(AuditingEntityListener.class)
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "goal_name", nullable = false, length = 200)
    private String goalName;

    @Column(name = "target_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "current_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    private SavingsGoal(User user, String goalName, BigDecimal targetAmount, String currency) {
        this.user = user;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currency = currency;
    }

    public static SavingsGoal of(User user, String goalName, BigDecimal targetAmount, String currency) {
        return new SavingsGoal(user, goalName, targetAmount, currency);
    }

    public void addAmount(BigDecimal amount) {
        this.currentAmount = this.currentAmount.add(amount);
    }

    public void subtractAmount(BigDecimal amount) {
        BigDecimal newAmount = this.currentAmount.subtract(amount);
        this.currentAmount = newAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : newAmount;
    }

    public BigDecimal getRemainingAmount() {
        BigDecimal remaining = targetAmount.subtract(currentAmount);
        return remaining.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : remaining;
    }

    public BigDecimal getProgressPercentage() {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentAmount.divide(targetAmount, 4, RoundingMode.HALF_UP)
                          .multiply(BigDecimal.valueOf(100));
    }

    public boolean isGoalAchieved() {
        return currentAmount.compareTo(targetAmount) >= 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SavingsGoal savingsGoal)) return false;
        return Objects.equals(id, savingsGoal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}