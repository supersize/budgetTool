package com.example.budgetTool.model.enums;


public enum NotificationType implements EnumCodeSupport {
    TRANSACTION("TRANSACTION", "Notice related to transaction"),
    SYSTEM("SYSTEM", "Notice related to system"),
    PROMOTIONAL("PROMOTIONAL", "Notice related to promotion"),
    SECURITY("SECURITY", "Notice related to security");

    private final String code;
    private final String text;

    private NotificationType(String code, String text) {
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

    public static NotificationType codeOf(String code) {
        if (code == null || "".equals(code)) return null;

        NotificationType found = null;

        for (NotificationType finding : NotificationType.values()) {
            if (finding.code.equals(code)) {
                found = finding;
                break;
            }
        }

        return found;
    }

    public static NotificationType textOf(String text) {
        if (text == null || "".equals(text)) return null;

        NotificationType found = null;

        for (NotificationType finding : NotificationType.values()) {
            if (finding.text.equals(text)) {
                found = finding;
                break;
            }
        }

        return found;
    }
}
