package com.example.budgetTool.model.enums;


public enum TempCodeType implements EnumCodeSupport {
    EMAIL_VERIFY("EMAIL_VERIFY", "Temporary Code for email verification"),
    PASSWORD_RESET("PASSWORD_RESET", "Temporary Code for password reset");

    private final String code;
    private final String text;

    private TempCodeType(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getName() {
        return this.name();
    }

    @Override
    public String getCode() {
        return this.code;
    }

    public String getText() {
        return this.text;
    }

    public static TempCodeType codeOf(String code) {
        if (code == null || "".equals(code)) return null;

        TempCodeType found = null;

        for (TempCodeType finding : TempCodeType.values()) {
            if (finding.code.equals(code)) {
                found = finding;
                break;
            }
        }

        return found;
    }

    public static TempCodeType textOf(String text) {
        if (text == null || "".equals(text)) return null;

        TempCodeType found = null;

        for (TempCodeType finding : TempCodeType.values()) {
            if (finding.text.equals(text)) {
                found = finding;
                break;
            }
        }

        return found;
    }
}
