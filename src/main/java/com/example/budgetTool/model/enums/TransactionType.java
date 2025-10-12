package com.example.budgetTool.model.enums;

import lombok.RequiredArgsConstructor;
import lombok.Getter;


@Getter
@RequiredArgsConstructor
public enum TransactionType {
    DEPOSIT("입금", "계좌로 돈이 들어오는 거래"),
    WITHDRAWAL("출금", "계좌에서 돈이 나가는 거래"),
    TRANSFER_OUT("송금", "다른 계좌로 돈을 보내는 거래"),
    TRANSFER_IN("입금", "다른 계좌에서 돈을 받는 거래");

    private final String description;
    private final String detail;
}