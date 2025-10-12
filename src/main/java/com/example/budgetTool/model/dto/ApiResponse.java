package com.example.budgetTool.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String timestamp;

    public static <T>  ApiResponse<T> SUCCESS (T data) {
        return new ApiResponse<T>(true, "SUCCESS", data, LocalDateTime.now().toString());
    }

    public static <T> ApiResponse<T> ERROR (String message) {
        return new ApiResponse<T>(false, message, null, LocalDateTime.now().toString());
    }

}
