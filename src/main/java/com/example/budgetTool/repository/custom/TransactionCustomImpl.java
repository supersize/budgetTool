package com.example.budgetTool.repository.custom;

import com.example.budgetTool.model.entity.QTransaction;
import com.example.budgetTool.model.entity.Transaction;
import com.example.budgetTool.utils.querydsl.FieldCondition;
import com.example.budgetTool.utils.querydsl.QuerydslUtil;
import com.example.budgetTool.utils.querydsl.SortCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.budgetTool.model.entity.QTransaction.transaction;

/**
 * packageName    : com.example.budgetTool.repository.custom
 * author         : kimjaehyeong
 * date           : 12/07/25
 * description    : Transaction Custom Repository Implementation
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/07/25        kimjaehyeong       created
 */
@RequiredArgsConstructor
public class TransactionCustomImpl implements TransactionCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean exist(List<FieldCondition> fconds) {
        BooleanBuilder builder = QuerydslUtil.customBooleanBuilder(Transaction.class, fconds);

        Integer queryResult = queryFactory
                .selectOne()
                .from(transaction)
                .where(builder)
                .fetchFirst();

        return queryResult != null;
    }

    @Override
    public Transaction getTransaction(List<FieldCondition> fconds, List<SortCondition> sconds) {
        BooleanBuilder fieldBuilder = QuerydslUtil.customBooleanBuilder(Transaction.class, fconds);
        OrderSpecifier[] sortBuilder = QuerydslUtil.customOrderSpecifiers(Transaction.class, sconds);

        return queryFactory
                .selectFrom(transaction)
                .where(fieldBuilder)
                .orderBy(sortBuilder)
                .fetchOne();
    }

    @Override
    public List<Transaction> getTransactionList(List<FieldCondition> fconds, List<SortCondition> sconds) {
        BooleanBuilder fieldBuilder = QuerydslUtil.customBooleanBuilder(Transaction.class, fconds);
        OrderSpecifier[] sortBuilder = QuerydslUtil.customOrderSpecifiers(Transaction.class, sconds);

        return queryFactory
                .selectFrom(transaction)
                .where(fieldBuilder)
                .orderBy(sortBuilder)
                .fetch();
    }
}
