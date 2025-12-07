package com.example.budgetTool.model.enums.converter;

import com.example.budgetTool.model.enums.AccountType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * packageName    : com.example.budgetTool.model.enums.converter
 * author         : kimjaehyeong
 * date           : 12/7/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/7/25        kimjaehyeong       created
 */
@Converter(autoApply = true)
public class AccountTypeConverter implements AttributeConverter<AccountType, String> {

    /**
     * @param accountType
     * @return
     */
    @Override
    public String convertToDatabaseColumn(AccountType accountType) {
        if (accountType == null) return null;
        // 객체에서 DB column으로 변경
        boolean isVaild = false;
        for (AccountType type : AccountType.values())
            if (type.getCode() == accountType.getCode()) isVaild = true;

        if (!isVaild) new IllegalAccessException();

        return accountType.getCode();
    }

    /**
     * @param dbData
     * @return
     */
    @Override
    public AccountType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        // DB column에서 객체로 변경
        boolean isVaild = false;
        for (AccountType type : AccountType.values())
            if (type.getCode() == dbData) isVaild = true;

        if (!isVaild) new IllegalAccessException();

        return AccountType.codeOf(dbData);
    }
}
