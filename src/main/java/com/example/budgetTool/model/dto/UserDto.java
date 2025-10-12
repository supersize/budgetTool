package com.example.budgetTool.model.dto;

import com.example.budgetTool.model.entity.User;
import com.example.budgetTool.model.enums.TempCodeType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate dateOfBirth,
        Boolean emailVerified,
        String tempCode,
        TempCodeType tempCodeType,
        LocalDateTime tempCodeExpires,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean isActive
) {

    @Builder
    public UserDto {}

    public static UserDto from(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .emailVerified(user.getEmailVerified())
                .tempCode(user.getTempCode())
                .tempCodeType(user.getTempCodeType())
                .tempCodeExpires(user.getTempCodeExpires())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .isActive(user.getIsActive())
                .build();
    }

    public record Request(
            String email,
            String password,
            String firstName,
            String lastName,
            String phoneNumber,
            LocalDate dateOfBirth
    ) {
        public User toEntity(String passwordHash) {
            return User.of(email, passwordHash, firstName, lastName, dateOfBirth);
        }
    }

    public record Response(
            Long id,
            String email,
            String firstName,
            String lastName,
            String fullName,
            String phoneNumber,
            LocalDate dateOfBirth,
            Boolean emailVerified,
            Boolean isActive,
            LocalDateTime createdAt
    ) {
        public static Response from(UserDto dto) {
            return new Response(
                    dto.id(),
                    dto.email(),
                    dto.firstName(),
                    dto.lastName(),
                    dto.firstName() + " " + dto.lastName(),
                    dto.phoneNumber(),
                    dto.dateOfBirth(),
                    dto.emailVerified(),
                    dto.isActive(),
                    dto.createdAt()
            );
        }
    }

    public record Summary(
            Long id,
            String email,
            String fullName,
            Boolean emailVerified
    ) {
        public static Summary from(UserDto dto) {
            return new Summary(
                    dto.id(),
                    dto.email(),
                    dto.firstName() + " " + dto.lastName(),
                    dto.emailVerified()
            );
        }
    }
}