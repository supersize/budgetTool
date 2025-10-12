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
@Builder
@Data
@AllArgsConstructor
public class SortCondition {
    private String field;
    @Builder.Default
    private SortDirection direction = SortDirection.ASC; // 기본값

    public enum SortDirection {
        ASC, DESC
    }
}
