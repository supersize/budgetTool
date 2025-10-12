package com.example.budgetTool.model.enums;

import lombok.RequiredArgsConstructor;
import lombok.Getter;


@Getter
@RequiredArgsConstructor
public enum AccountType {
    SAVINGS("저축계좌", "일반 저축 계좌"),
    CHECKING("당좌계좌", "당좌 예금 계좌"),
    BUSINESS("기업계좌", "기업용 계좌");

    private final String description;
    private final String detail;
}