package com.example.budgetTool.utils.querydsl;

import lombok.AllArgsConstructor;
import lombok.Data;

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
@Data
@AllArgsConstructor
public class SearchCondition {
    List<FieldCondition> condition;
}
