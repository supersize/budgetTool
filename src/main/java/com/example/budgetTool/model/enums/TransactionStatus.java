package com.example.budgetTool.model.enums;


public enum TransactionStatus implements EnumCodeSupport {
    PENDING("PENDING", "Pending"),
    COMPLETED("COMPLETED", "Completed"),
    FAILED("FAILED", "Failed"),
    CANCELLED("CANCELLED", "Cancelled");

    private final String code;
    private final String text;

    private TransactionStatus(String code, String text) {
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

    public static TransactionStatus codeOf(String code) {
        if (code == null || "".equals(code)) return null;

        TransactionStatus found = null;

        for (TransactionStatus finding : TransactionStatus.values()) {
            if (finding.code.equals(code)) {
                found = finding;
                break;
            }
        }

        return found;
    }

    public static TransactionStatus textOf(String text) {
        if (text == null || "".equals(text)) return null;

        TransactionStatus found = null;

        for (TransactionStatus finding : TransactionStatus.values()) {
            if (finding.text.equals(text)) {
                found = finding;
                break;
            }
        }

        return found;
    }
}
