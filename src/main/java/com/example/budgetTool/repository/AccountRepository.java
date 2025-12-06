package com.example.budgetTool.repository;

import com.example.budgetTool.model.entity.Account;
import com.example.budgetTool.repository.custom.AccountCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
