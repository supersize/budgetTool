package com.example.budgetTool.repository.custom;

import com.example.budgetTool.model.entity.User;
import com.example.budgetTool.utils.querydsl.FieldCondition;
import com.example.budgetTool.utils.querydsl.SortCondition;

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
public interface UserCustom {

    boolean exist (List<FieldCondition> fconds);


    /**
     * get a user matched with conditions
     * @param fconds
     * @param sconds
     * @return user
     */
    User getUser(List<FieldCondition> fconds, List<SortCondition> sconds);


}
