package com.example.budgetTool.model.enums.converter;

import com.example.budgetTool.model.enums.TransactionStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * packageName    : com.example.budgetTool.model.enums.converter
 * author         : kimjaehyeong
 * date           : 12/7/25
 * description    : JPA Converter for TransactionStatus enum
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/7/25        kimjaehyeong       created
 */
@Converter(autoApply = true)
public class TransactionStatusConverter implements AttributeConverter<TransactionStatus, String> {

    /**
     * Convert enum to database column
     * @param transactionStatus
     * @return
     */
    @Override
    public String convertToDatabaseColumn(TransactionStatus transactionStatus) {
        if (transactionStatus == null) return null;
        // 객체에서 DB column으로 변경
        boolean isValid = false;
        for (TransactionStatus status : TransactionStatus.values())
            if (status.getCode().equals(transactionStatus.getCode())) isValid = true;

        if (!isValid) new IllegalAccessException();

        return transactionStatus.getCode();
    }

    /**
     * Convert database column to enum
     * @param dbData
     * @return
     */
    @Override
    public TransactionStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        // DB column에서 객체로 변경
        boolean isValid = false;
        for (TransactionStatus status : TransactionStatus.values())
            if (status.getCode().equals(dbData)) isValid = true;

        if (!isValid) new IllegalAccessException();

        return TransactionStatus.codeOf(dbData);
    }
}
