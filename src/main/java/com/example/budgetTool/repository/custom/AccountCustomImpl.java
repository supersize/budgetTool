package com.example.budgetTool.repository.custom;

import static com.example.budgetTool.model.entity.QAccount.account;

import com.example.budgetTool.model.entity.Account;
import com.example.budgetTool.utils.querydsl.FieldCondition;
import com.example.budgetTool.utils.querydsl.QuerydslUtil;
import com.example.budgetTool.utils.querydsl.SortCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * packageName    : com.example.budgetTool.repository.custom
 * author         : kimjaehyeong
 * date           : 12/05/25
 * description    : Account Custom Repository Implementation
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/05/25        kimjaehyeong       created
 */
@RequiredArgsConstructor
public class AccountCustomImpl implements AccountCustom {
    
    private final JPAQueryFactory queryFactory;

    @Override
    public boolean exist(List<FieldCondition> fconds) {
        BooleanBuilder builder = QuerydslUtil.customBooleanBuilder(Account.class, fconds);
        
        Integer queryResult = queryFactory
                .selectOne()
                .from(account)
                .where(builder)
                .fetchFirst();
        
        return queryResult != null;
    }

    @Override
    public Account getAccount(List<FieldCondition> fconds, List<SortCondition> sconds) {
        BooleanBuilder fieldBuilder = QuerydslUtil.customBooleanBuilder(Account.class, fconds);
        OrderSpecifier[] sortBuilder = QuerydslUtil.customOrderSpecifiers(Account.class, sconds);

        return queryFactory
                .selectFrom(account)
                .where(fieldBuilder)
                .orderBy(sortBuilder)
                .fetchOne();
    }

    @Override
    public List<Account> getAccountList(List<FieldCondition> fconds, List<SortCondition> sconds) {
        BooleanBuilder fieldBuilder = QuerydslUtil.customBooleanBuilder(Account.class, fconds);
        OrderSpecifier[] sortBuilder = QuerydslUtil.customOrderSpecifiers(Account.class, sconds);

        return queryFactory
                .selectFrom(account)
                .where(fieldBuilder)
                .orderBy(sortBuilder)
                .fetch();
    }
}
