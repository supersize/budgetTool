package com.example.budgetTool.repository;

import com.example.budgetTool.model.entity.Transaction;
import com.example.budgetTool.repository.custom.TransactionCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * packageName    : com.example.budgetTool.repository
 * author         : kimjaehyeong
 * date           : 12/07/25
 * description    : Transaction Repository
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/07/25        kimjaehyeong       created
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, TransactionCustom {
    
    /**
     * Find all transactions for a specific user's accounts
     * @param userId User ID
     * @return List of transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId ORDER BY t.createdAt DESC")
    List<Transaction> findByUserId(@Param("userId") Long userId);
    
    /**
     * Find transactions by account ID
     * @param accountId Account ID
     * @return List of transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId ORDER BY t.createdAt DESC")
    List<Transaction> findByAccountId(@Param("accountId") Long accountId);
    
    /**
     * Find transactions by date range for a user
     * @param userId User ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId " +
           "AND t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC")
    List<Transaction> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
