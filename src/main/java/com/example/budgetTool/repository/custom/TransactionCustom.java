package com.example.budgetTool.repository.custom;

import com.example.budgetTool.model.entity.Transaction;
import com.example.budgetTool.utils.querydsl.FieldCondition;
import com.example.budgetTool.utils.querydsl.SortCondition;

import java.util.List;

/**
 * packageName    : com.example.budgetTool.repository.custom
 * author         : kimjaehyeong
 * date           : 12/07/25
 * description    : Transaction Custom Repository Interface
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/07/25        kimjaehyeong       created
 */
public interface TransactionCustom {
    boolean exist(List<FieldCondition> fconds);
    Transaction getTransaction(List<FieldCondition> fconds, List<SortCondition> sconds);
    List<Transaction> getTransactionList(List<FieldCondition> fconds, List<SortCondition> sconds);
}
