package com.example.budgetTool.repository.custom;

import com.example.budgetTool.model.entity.Account;
import com.example.budgetTool.utils.querydsl.FieldCondition;
import com.example.budgetTool.utils.querydsl.SortCondition;

import java.util.List;

/**
 * packageName    : com.example.budgetTool.repository.custom
 * author         : kimjaehyeong
 * date           : 12/05/25
 * description    : Account Custom Repository Interface
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/05/25        kimjaehyeong       created
 */
public interface AccountCustom {

    /**
     * Check if account exists with given conditions
     * @param fconds Field conditions
     * @return true if exists, false otherwise
     */
    boolean exist(List<FieldCondition> fconds);

    /**
     * Get a single account matched with conditions
     * @param fconds Field conditions
     * @param sconds Sort conditions
     * @return Account entity
     */
    Account getAccount(List<FieldCondition> fconds, List<SortCondition> sconds);

    /**
     * Get list of accounts matched with conditions
     * @param fconds Field conditions
     * @param sconds Sort conditions
     * @return List of Account entities
     */
    List<Account> getAccountList(List<FieldCondition> fconds, List<SortCondition> sconds);
}
