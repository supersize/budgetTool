package com.example.budgetTool.model.enums.converter;

import com.example.budgetTool.model.enums.TransactionType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * packageName    : com.example.budgetTool.model.enums.converter
 * author         : kimjaehyeong
 * date           : 12/7/25
 * description    : JPA Converter for TransactionType enum
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/7/25        kimjaehyeong       created
 */
@Converter(autoApply = true)
public class TransactionTypeConverter implements AttributeConverter<TransactionType, String> {

    /**
     * Convert enum to database column
     * @param transactionType
     * @return
     */
    @Override
    public String convertToDatabaseColumn(TransactionType transactionType) {
        if (transactionType == null) return null;
        // 객체에서 DB column으로 변경
        boolean isValid = false;
        for (TransactionType type : TransactionType.values())
            if (type.getCode().equals(transactionType.getCode())) isValid = true;

        if (!isValid) new IllegalAccessException();

        return transactionType.getCode();
    }

    /**
     * Convert database column to enum
     * @param dbData
     * @return
     */
    @Override
    public TransactionType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        // DB column에서 객체로 변경
        boolean isValid = false;
        for (TransactionType type : TransactionType.values())
            if (type.getCode().equals(dbData)) isValid = true;

        if (!isValid) new IllegalAccessException();

        return TransactionType.codeOf(dbData);
    }
}
