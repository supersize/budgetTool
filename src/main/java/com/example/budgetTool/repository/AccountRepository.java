package com.example.budgetTool.repository;

import com.example.budgetTool.model.entity.Account;
import com.example.budgetTool.repository.custom.AccountCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * packageName    : com.example.budgetTool.repository
 * author         : kimjaehyeong
 * date           : 12/05/25
 * description    : Account Repository
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/05/25        kimjaehyeong       created
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, AccountCustom {
    
    /**
     * Find all accounts by user ID, ordered by creation date descending
     * @param userId User ID
     * @return List of accounts
     */
    @Query("SELECT a FROM Account a WHERE a.user.id = :userId ORDER BY a.createdAt DESC")
    List<Account> findByUserId(@Param("userId") Long userId);
}
