package com.example.budgetTool.model.dto;

/**
 * packageName    : com.example.budgetTool.model.dto
 * author         : kimjaehyeong
 * date           : 12/3/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/3/25        kimjaehyeong       created
 */
public class UserRecord {

    public record forgotPassword(String email, String opt) {}
}
