package com.example.budgetTool.model.entity;

import com.example.budgetTool.model.enums.TempCodeType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
public class User implements UserDetails {

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

    @Column(name = "occupation", nullable = false)
    private String occupation;

    @Column(name = "income_range", nullable = false)
    private String incomeRange;

//    @Column(name = "financial_goals", nullable = false)
//    private List<String> financialGoals;

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

    @Transient
    private String otp;

    private User(String email, String passwordHash, String firstName, String lastName
            , String phoneNumber, LocalDate dateOfBirth, Boolean emailVerified, String occupation
            , String incomeRange, String otp) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.emailVerified = emailVerified;
        this.occupation = occupation;
        this.incomeRange = incomeRange;
        this.otp = otp;
    }

    public static User of(String email, String passwordHash, String firstName, String lastName
            , String phoneNumber, LocalDate dateOfBirth, Boolean emailVerified
            , String occupation, String incomeRange, String otp) {
        return new User(email, passwordHash, firstName, lastName, phoneNumber, dateOfBirth, emailVerified
                , occupation, incomeRange, otp);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // For Thymeleaf sec:authentication="principal.name"
    public String getName() {
        return getFullName();
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

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
//        return this.firstName + " " + this.lastName;
        return this.email;
    }
}