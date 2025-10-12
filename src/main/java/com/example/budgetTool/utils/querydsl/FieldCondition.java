package com.example.budgetTool.utils.querydsl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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

@Data
@AllArgsConstructor
@Builder
public class FieldCondition {
    private String field;
    private	Operator operator;
    private Object value;
    private LogicType logic;

//		예시1. BETWEEN 쓰려면 min, max로 이뤄진 MAP을 줘야됨..
//		Map<String, String> between = new HashMap<>();
//		between.put("min", sc.getSearchStartDate());
//		between.put("max", sc.getSearchEndDate());
//		clist.add(new FieldCondition( "filmingStartDate", Operator.BETWEEN, between, LogicType.AND));
//
//		예시2.  AND( a=b OR c=b )
//		List<FieldCondition> clist2 = new ArrayList<>();
//		clist2.add(new FieldCondition( "imageSubject", Operator.LIKE, sc.getKeyword(), LogicType.OR ));
//		clist2.add(new FieldCondition( "administrativeDistrictName", Operator.LIKE, sc.getKeyword(), LogicType.OR ));
//		BooleanBuilder booleanBuilder2 = QuerydslUtil.customBooleanBuilder( ImageInfo.class, clist2);
//		booleanBuilder.and(booleanBuilder2);

//		예시3. join 테이블 컬럼을 조건으로 사용하고 싶을 때
//		예시4. q클래스의 alias 사용하고 싶을 때 : QuerydslUtil.customBooleanBuilder 매개변수 Qclass.getMetadata() 추가
//		List<FieldCondition> clist2 = new ArrayList<>();
//		clist2.add(new FieldCondition( "institutionCode", Operator.GOE, "15410000", LogicType.AND ));
//		BooleanBuilder joinbb = QuerydslUtil.customBooleanBuilder( InstitutionCode.class, clist2, institutionCode1.getMetadata() );
//		booleanBuilder.and(joinbb);


}
