package com.example.budgetTool.model.enums;

import lombok.RequiredArgsConstructor;
import lombok.Getter;


@Getter
@RequiredArgsConstructor
public enum NotificationType {
    TRANSACTION("거래", "거래 관련 알림"),
    SYSTEM("시스템", "시스템 관련 알림"),
    PROMOTIONAL("홍보", "마케팅 및 홍보 알림"),
    SECURITY("보안", "보안 관련 알림");

    private final String description;
    private final String detail;
}