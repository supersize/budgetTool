package com.example.budgetTool.repository.custom;

import static com.example.budgetTool.model.entity.QUser.user;

import com.example.budgetTool.model.entity.QUser;
import com.example.budgetTool.model.entity.User;
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
 * date           : 10/22/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 10/22/25        kimjaehyeong       created
 */
@RequiredArgsConstructor
public class UserCustomImpl implements UserCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public boolean exist(List<FieldCondition> fconds) {
        BooleanBuilder builder = QuerydslUtil.customBooleanBuilder(User.class, fconds);

        Integer queryResult = queryFactory.selectOne().from(user).where(builder).fetchFirst();
        return queryResult != null;
    }

    @Override
    public User getUser(List<FieldCondition> fconds, List<SortCondition> sconds) {
        BooleanBuilder fieldBuilder = QuerydslUtil.customBooleanBuilder(User.class, fconds);
        OrderSpecifier[] sortBuilder = QuerydslUtil.customOrderSpecifiers(User.class, sconds);


        User result = queryFactory.selectFrom(user).where(fieldBuilder).orderBy(sortBuilder).fetchOne();

        return result;
    }
}
