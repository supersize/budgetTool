package com.example.budgetTool.model.enums;

import lombok.RequiredArgsConstructor;
import lombok.Getter;


public enum AccountType implements EnumCodeSupport {
    SAVINGS("SAVINGS", "Saving Account"),
    CHECKING("CHECKING", "Checking Account"),
    BUSINESS("BUSINESS", "Business Account");

    /** 코드. */
    private final String code;

    /** 코드명. */
    private final String text;

    /** 항목명. */
//    private final String label;

    /**
     * 사용자역할 Enum 생성.
     *
     * @param text 명칭.
     */
    private AccountType(String code, String text) {
        this.code = code;
        this.text = text;
    }

    /**
     * Enum 명칭을 얻는다.
     *
     * @return Enum 명칭.
     */
    public String getName () {
        return this.name();
    }

    /**
     * 코드를 얻는다.
     *
     * @return 코드.
     */
    @Override
    public String getCode () {
        return this.code;
    }

    /**
     * 코드명을 얻는다.
     *
     * @return 코드명.
     */
    public String getText() {
        return this.text;
    }

    /**
     * 레이블명을 얻는다.
     *
     * @return 레이블명.
    public String getLabel() {
        return this.label;
    }
     */

    /**
     * 주어진 코드 값에 해당하는 actionTypeCode 돌려 준다.
     * 코드에 해당하는 항목이 없으면 null을 돌려 준다.
     *
     * @param code actionTypeCode 찾으려는 코드값.
     * @return
     */
    public static AccountType codeOf(String code) {
        if ( code == null || "".equals(code)) return null;

        AccountType found = null;

        for (AccountType finding : AccountType.values()) {
            if (finding.code.equals(code)) {
                found = finding;
                break;
            }
        }

        return found;
    }

    /**
     * 주어진 코드 값에 해당하는 actionType 돌려 준다.
     * 코드에 해당하는 항목이 없으면 null을 돌려 준다.
     *
     * @param text actionType 찾으려는 코드값.
     * @return
     */
    public static AccountType textOf(String text) {
        if ( text == null || "".equals(text)) return null;

        AccountType found = null;

        for (AccountType finding : AccountType.values()) {
            if (finding.text.equals(text)) {
                found = finding;
                break;
            }
        }

        return found;
    }

    /**
     * 주어진 레이블 값에 해당하는 actionType 돌려 준다.
     * 레이블에 해당하는 항목이 없으면 null을 돌려 준다.
     * @param label
     * @return ActionType
     * @author 김재형
     * @date 2025-04-23
    public static AccountType labelOf(String label) {
        if ( label == null || "".equals(label)) return null;
        AccountType found = null;
        for (AccountType finding : AccountType.values()) {
            if (finding.label.equals(label)) {
                found = finding;
                break;
            }
        }
        return found;
    }
     */
}