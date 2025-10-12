package com.example.budgetTool.utils.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.PathMetadata;

import java.util.List;

/**
 * packageName    : com.example.budgetTool.utils.querydsl
 * author         : kimjaehyeong
 * date           : 9/23/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 9/23/25        kimjaehyeong       created
 */
public class QuerydslUtil {
    /**
     * where 절 booleanBuilder
     * @clazz 엔티티 클래스
     * @clist 조건
     */
    public static BooleanBuilder customBooleanBuilder(Class<?> clazz, List<FieldCondition> clist ) {
        if(clazz == null) throw new NullPointerException("Class<?> clazz can't be null");
        if(clist == null || clist.isEmpty()) return null;

        return customBooleanBuilder(clazz, clist, null);
    }

    /**
     * where 절 booleanBuilder
     *
     * @clazz 엔티티 클래스
     * @metadata Q클래스의 메타데이터 : alias 지정
     */
    public static BooleanBuilder customBooleanBuilder( Class<?> clazz, List<FieldCondition> clist, PathMetadata metadata ) {
        if(clazz == null) throw new NullPointerException("Class<?> clazz can't be null");
        if(clist == null || clist.isEmpty()) return null;

        GenericPredicateBuilder<?> gpb = getGenericPredicateBuilder(clazz, metadata);

        SearchCondition searchCondition = new SearchCondition(clist);
        BooleanBuilder booleanBuilder = (BooleanBuilder) gpb.buildWhere( searchCondition );

        return booleanBuilder;
    }

    /**
     * orderby절
     * @clazz 엔티티 클래스
     * @metadata Q클래스의 메타데이터
     */
    public static OrderSpecifier[] customOrderSpecifiers(Class<?> clazz, List<SortCondition> sortList ){
        if(clazz == null ) throw new NullPointerException("Class<?> clazz can't be null");
//		if(sortList == null || sortList.isEmpty()) return null;
        if(sortList == null || sortList.isEmpty()) return new OrderSpecifier<?>[0];;

        return customOrderSpecifiers(clazz, sortList, null);
    }

    public static OrderSpecifier[] customOrderSpecifiers( Class<?> clazz, List<SortCondition> sortList, PathMetadata metadata ){
        if(clazz == null ) throw new NullPointerException("Class<?> clazz can't be null");
//		if(sortList == null || sortList.isEmpty()) return null;
        if(sortList == null || sortList.isEmpty()) return new OrderSpecifier<?>[0];;

        GenericPredicateBuilder<?> gpb = getGenericPredicateBuilder(clazz, metadata);

        List<OrderSpecifier<?>> orders = gpb.buildOrderBy( sortList );
        return orders.toArray( new OrderSpecifier[0] );
    }

    /**
     * GenericPredicateBuilder 생성
     *
     * @clazz 엔티티 클래스
     * @metadata Q클래스의 메타데이터 : null이 아닌경우 q클래스의 메타데이터를 사용함
     *
     */
    private static GenericPredicateBuilder<?> getGenericPredicateBuilder(Class<?> clazz, PathMetadata metadata) {
        if(clazz == null ) throw new NullPointerException("Class<?> clazz or metadata can't be null");

        GenericPredicateBuilder<?> gpb;
        if( metadata == null ) {
            gpb = new GenericPredicateBuilder<>(clazz);
        } else {
            gpb = new GenericPredicateBuilder<>(clazz, metadata);

        }

        return gpb;
    }
}
