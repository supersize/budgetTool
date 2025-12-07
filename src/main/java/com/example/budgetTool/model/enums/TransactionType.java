package com.example.budgetTool.model.enums;


public enum TransactionType implements EnumCodeSupport {
    DEPOSIT("DEPOSIT", "A transaction which money comes into your account."),
    WITHDRAWAL("WITHDRAWAL", "A transaction which money goes out from your account."),
    TRANSFER_OUT("TRANSFER_OUT", "A transaction which your money goes to other account."),
    TRANSFER_IN("TRANSFER_IN", "A transaction which your money comes into other account.");

    private final String code;
    private final String text;

    private TransactionType(String code, String text) {
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

    public static TransactionType codeOf(String code) {
        if (code == null || "".equals(code)) return null;

        TransactionType found = null;

        for (TransactionType finding : TransactionType.values()) {
            if (finding.code.equals(code)) {
                found = finding;
                break;
            }
        }

        return found;
    }

    public static TransactionType textOf(String text) {
        if (text == null || "".equals(text)) return null;

        TransactionType found = null;

        for (TransactionType finding : TransactionType.values()) {
            if (finding.text.equals(text)) {
                found = finding;
                break;
            }
        }

        return found;
    }
}
