package com.example.budgetTool.model.enums.converter;

import com.example.budgetTool.model.enums.NotificationType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * packageName    : com.example.budgetTool.model.enums.converter
 * author         : kimjaehyeong
 * date           : 12/7/25
 * description    : JPA Converter for NotificationType enum
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/7/25        kimjaehyeong       created
 */
@Converter(autoApply = true)
public class NotificationTypeConverter implements AttributeConverter<NotificationType, String> {

    /**
     * Convert enum to database column
     * @param notificationType
     * @return
     */
    @Override
    public String convertToDatabaseColumn(NotificationType notificationType) {
        if (notificationType == null) return null;
        // 객체에서 DB column으로 변경
        boolean isValid = false;
        for (NotificationType type : NotificationType.values())
            if (type.getCode().equals(notificationType.getCode())) isValid = true;

        if (!isValid) new IllegalAccessException();

        return notificationType.getCode();
    }

    /**
     * Convert database column to enum
     * @param dbData
     * @return
     */
    @Override
    public NotificationType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        // DB column에서 객체로 변경
        boolean isValid = false;
        for (NotificationType type : NotificationType.values())
            if (type.getCode().equals(dbData)) isValid = true;

        if (!isValid) new IllegalAccessException();

        return NotificationType.codeOf(dbData);
    }
}
