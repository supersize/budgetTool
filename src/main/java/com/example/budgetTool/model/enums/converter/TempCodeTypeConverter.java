package com.example.budgetTool.model.enums.converter;

import com.example.budgetTool.model.enums.TempCodeType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * packageName    : com.example.budgetTool.model.enums.converter
 * author         : kimjaehyeong
 * date           : 12/7/25
 * description    : JPA Converter for TempCodeType enum
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/7/25        kimjaehyeong       created
 */
@Converter(autoApply = true)
public class TempCodeTypeConverter implements AttributeConverter<TempCodeType, String> {

    /**
     * Convert enum to database column
     * @param tempCodeType
     * @return
     */
    @Override
    public String convertToDatabaseColumn(TempCodeType tempCodeType) {
        if (tempCodeType == null) return null;
        // 객체에서 DB column으로 변경
        boolean isValid = false;
        for (TempCodeType type : TempCodeType.values())
            if (type.getCode().equals(tempCodeType.getCode())) isValid = true;

        if (!isValid) new IllegalAccessException();

        return tempCodeType.getCode();
    }

    /**
     * Convert database column to enum
     * @param dbData
     * @return
     */
    @Override
    public TempCodeType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        // DB column에서 객체로 변경
        boolean isValid = false;
        for (TempCodeType type : TempCodeType.values())
            if (type.getCode().equals(dbData)) isValid = true;

        if (!isValid) new IllegalAccessException();

        return TempCodeType.codeOf(dbData);
    }
}
