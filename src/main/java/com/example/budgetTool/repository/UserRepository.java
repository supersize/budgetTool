package com.example.budgetTool.repository;

import com.example.budgetTool.model.entity.User;
import com.example.budgetTool.repository.custom.UserCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * packageName    : com.example.budgetTool.repository
 * author         : kimjaehyeong
 * date           : 10/22/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 10/22/25        kimjaehyeong       created
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserCustom {
}
