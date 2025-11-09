package com.example.budgetTool.model.entity;

import com.example.budgetTool.model.enums.TempCodeType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "temp_code", length = 10)
    private String tempCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "temp_code_type")
    private TempCodeType tempCodeType;

    @Column(name = "temp_code_expires")
    private LocalDateTime tempCodeExpires;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "salt", nullable = false)
    private String salt;

    private User(String email, String passwordHash, String firstName, String lastName, LocalDate dateOfBirth) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }

    public static User of(String email, String passwordHash, String firstName, String lastName, LocalDate dateOfBirth) {
        return new User(email, passwordHash, firstName, lastName, dateOfBirth);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void setTempCode(String code, TempCodeType type, LocalDateTime expires) {
        this.tempCode = code;
        this.tempCodeType = type;
        this.tempCodeExpires = expires;
    }

    public boolean isTempCodeValid() {
        return tempCode != null && tempCodeExpires != null && tempCodeExpires.isAfter(LocalDateTime.now());
    }

    public void clearTempCode() {
        this.tempCode = null;
        this.tempCodeType = null;
        this.tempCodeExpires = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}