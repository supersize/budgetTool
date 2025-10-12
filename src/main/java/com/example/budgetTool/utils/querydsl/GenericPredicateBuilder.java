package com.example.budgetTool.utils.querydsl;

import com.example.budgetTool.model.enums.EnumCodeSupport;
import com.example.budgetTool.utils.EnumUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.SimpleExpression;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
public class GenericPredicateBuilder<T> {
    private final PathBuilder<T> pathBuilder;

    /**
     * @param clazz 엔티티 클래스 주입하여 pathBuilder를 동적경로로 구성
     */
    public GenericPredicateBuilder(Class<T> clazz) {
        // camelCase alias
        String simpleName = clazz.getSimpleName();
        String camelName = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);

        this.pathBuilder = new PathBuilder<>(clazz, camelName);
    }

    public GenericPredicateBuilder(Class<T> clazz, PathMetadata metaData) {
        this.pathBuilder = new PathBuilder<>(clazz, metaData);
    }

    /**
     * buildWhere
     *
     * where 절에 들어갈 predicate(booleanBuilder)
     */
    public Predicate buildWhere(SearchCondition condition){
        BooleanBuilder builder = new BooleanBuilder();

        for( FieldCondition fc : condition.getCondition() ) {
            if( fc.getValue() == null && !fc.getOperator().equals(Operator.IS_NULL) ) {
                continue;
            }

            Predicate p = createPredicate(fc);

            if( fc.getLogic() == LogicType.AND ) {
                builder.and(p);
            } else {
                builder.or(p);
            }
        }

        return builder;
    }

    /**
     * Operator 추가되면 여기 같이 추가
     *
     * @Operator.BETWEEN인 경우 min, max 키와 값을 가진 MAP을 줘야됨
     *
     */
    private Predicate createPredicate( FieldCondition fc ) {
        String field = fc.getField();
        Operator op = fc.getOperator();
        Object value = fc.getValue();

        Class<?> type;
        try {
            type = pathBuilder.getType().getDeclaredField(fc.getField()).getType();
            Object typeValue = convertValue(value, type);
            if( typeValue == null && op != Operator.IS_NULL ) return null;

            Path<?> path = pathBuilder.get(field);

            switch (op) {
                case EQ: return  Expressions.booleanTemplate("{0} = {1}", path, typeValue);
                case NE: return  Expressions.booleanTemplate("{0} != {1}", path, typeValue);
                case GT: return  Expressions.booleanTemplate("{0} > {1}", path, typeValue);
                case GOE: return  Expressions.booleanTemplate("{0} >= {1}", path, typeValue);
                case LT: return  Expressions.booleanTemplate("{0} < {1}", path, typeValue);
                case LOE: return  Expressions.booleanTemplate("{0} <= {1}", path, typeValue);
                case LIKE: return  Expressions.booleanTemplate("{0} like {1}", path, "%" + typeValue + "%");
                case START_WITH : return  Expressions.booleanTemplate("{0} like {1}", path, typeValue + "%");
                case IN :
                    SimpleExpression<String> se = (SimpleExpression<String>) pathBuilder.get(field, type);
                    if( typeValue instanceof Collection<?>) {
                        return se.in( (Collection<String>) typeValue);
                    } else {
                        return Expressions.booleanTemplate("1=0");
                    }
                case BETWEEN :
                    if( typeValue instanceof Map<?,?>) {
                        Object min = ((Map) typeValue).get("min");
                        Object max = ((Map) typeValue).get("max");
                        if( min != null && max != null ) {
                            return Expressions.booleanTemplate("{0} between {1} and {2}", path, Expressions.constant(min), Expressions.constant(max));
                        }
                    }
                    return Expressions.booleanTemplate("1=0");	// 잘못된 between
                case IS_NULL : return  Expressions.booleanTemplate("{0} is null", path, "");

                default: throw new IllegalArgumentException("Unsupported operator : " + op);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(" error : " + e);
        }
    }

    @SuppressWarnings("unchecked")
    private Object convertValue( Object value, Class<?> type ) {
        if (value == null) return null;

        // value가 String인 경우
        if (value instanceof String ) {
            return simpleCast(value, type);
        }

        // 이미 올바른 단일 타입
        if (type.isInstance(value)) return value;
        // map 인 경우
        if( value instanceof Map<?, ?> ) {
            Map<?, ?> map = (Map<?, ?>) value;
            Map<String, Object> result = new HashMap<>();
            for( Map.Entry<?, ?> entry : map.entrySet() ) {
                String key = entry.getKey().toString();
                Object casted = simpleCast(entry.getValue(), type);
                result.put(key, casted);
            }
            return result;
        }

        // 배열인 경우
        if( value.getClass().isArray() ) {
            Object[] array = (Object[]) value;
            List<T> list = new ArrayList<>();
            for( Object v : array ) {
                list.add((T) simpleCast(v, type));
            }
            return list;
        }

        // Collection인 경우
        if( value instanceof Collection<?> ) {
            Collection<?> col = (Collection<?>) value;
            List<T> list = new ArrayList<>();
            for( Object v : col ) {
                list.add((T) simpleCast(v, type));
            }
            return list;
        }

        return simpleCast(value, type);
    }

    @SuppressWarnings("unchecked")
    private static <T> T simpleCast(Object value, Class<T> type) {
        if( value == null ) return null;

        String str = value.toString();
        if( StringUtils.isEmpty(str) ) return null;

        if (type == String.class) return (T) str;
        if (type == Integer.class || type == int.class) return (T) Integer.valueOf(str);
        if (type == Long.class) return (T) Long.valueOf(str);
        // boolean.class 조건 추가.
        if (type == Boolean.class || type == boolean.class) return (T) Boolean.valueOf(str);
        if (type == java.util.Date.class || type == java.sql.Date.class) {
            try {
                // 기본 포맷: "yyyy-MM-dd"
                LocalDate localDate = LocalDate.parse(str, DateTimeFormatter.ISO_LOCAL_DATE);
                return (T) java.sql.Date.valueOf(localDate);
            } catch (Exception e) {
                System.out.println("날짜 파싱 실패: " + str + " " + e);
                return null;
            }
        }
        // enum일때 추가
        if(type.isEnum()) {
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) type;

            if (EnumCodeSupport.class.isAssignableFrom(type)) {
                return (T) EnumUtil.codeOf((Class<? extends EnumCodeSupport>) enumClass, value);
            }

            return (T) Enum.valueOf((Class<Enum>) type, value.toString());
        }
        throw new IllegalArgumentException("변환 불가: " + value + " → " + type);
    }

    /**
     * buildOrderBy
     *
     * 정렬기준
     */
    public List<OrderSpecifier<?>> buildOrderBy(List<SortCondition> sortList ){
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        for ( SortCondition sc : sortList ) {
            Path<?> path = pathBuilder.get(sc.getField());

            Order order = (sc.getDirection() == SortCondition.SortDirection.DESC) ? Order.DESC : Order.ASC;
            orders.add(new OrderSpecifier(order, path));
        }

        return orders;

    }

}
