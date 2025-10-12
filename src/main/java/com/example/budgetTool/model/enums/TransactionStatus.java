package com.example.budgetTool.model.enums;

import lombok.RequiredArgsConstructor;
import lombok.Getter;


@Getter
@RequiredArgsConstructor
public enum TransactionStatus {
    PENDING("대기중", "거래가 처리 대기 중인 상태"),
    COMPLETED("완료", "거래가 성공적으로 완료된 상태"),
    FAILED("실패", "거래가 실패한 상태"),
    CANCELLED("취소", "거래가 취소된 상태");

    private final String description;
    private final String detail;
}