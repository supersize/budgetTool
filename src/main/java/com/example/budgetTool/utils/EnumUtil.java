package com.example.budgetTool.utils;

import com.example.budgetTool.model.enums.EnumCodeSupport;

/**
 * packageName    : com.example.budgetTool.utils.querydsl
 * author         : kimjaehyeong
 * date           : 9/23/25
 * description    : 모든 Enum 클래스의 공통 사용 부분 정리한 클래스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 9/23/25        kimjaehyeong       created
 */
public class EnumUtil {
    @SuppressWarnings("uncheckec")
    public static <E extends Enum<E> & EnumCodeSupport<T>, T> E codeOf(Class<? extends EnumCodeSupport> enumClass, Object code) {
        if(code == null) return null;

        T found = null;
        for(EnumCodeSupport e: enumClass.getEnumConstants()) {
            if (e.getCode().equals(code)) return (E) e;
        }

        return null;
    }
}
