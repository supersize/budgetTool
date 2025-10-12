package com.example.budgetTool.model.enums;

import lombok.RequiredArgsConstructor;
import lombok.Getter;

@Getter
@RequiredArgsConstructor
public enum TempCodeType {
    EMAIL_VERIFY("이메일 인증", "이메일 주소 인증용 임시 코드"),
    PASSWORD_RESET("비밀번호 재설정", "비밀번호 재설정용 임시 코드");

    private final String description;
    private final String detail;
}