package com.example.budgetTool.controller.rest;

import com.example.budgetTool.model.dto.ApiResponse;
import com.example.budgetTool.model.dto.UserDto;
import com.example.budgetTool.model.dto.UserRecord;
import com.example.budgetTool.model.entity.User;
import com.example.budgetTool.service.RedisService;
import com.example.budgetTool.service.UserService;
import com.example.budgetTool.utils.JwtUtil;
import com.example.budgetTool.utils.ShaUtil;
import com.example.budgetTool.utils.querydsl.FieldCondition;
import com.example.budgetTool.utils.querydsl.LogicType;
import com.example.budgetTool.utils.querydsl.Operator;
import com.example.budgetTool.utils.querydsl.SortCondition;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/Auth")
public class AuthRestController {

    private final UserService userService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;

    /**
     * methodName : isEmailInUse
     * author : Jae-Hyeong Kim
     * description : check an input email exists or not
     *
     * @param inputEmail
     * @return api response
     */
    @GetMapping("/isEmailInUse")
    public ApiResponse<Boolean> isEmailInUse (@RequestParam(required = true) String inputEmail) {
        ApiResponse<Boolean> res = new ApiResponse<>();
        try {
            List<FieldCondition> fconds = new ArrayList<>();
            fconds.add(new FieldCondition("email", Operator.EQ, inputEmail, LogicType.AND));

            boolean result = this.userService.exist(fconds);
            res = ApiResponse.SUCCESS(result);
        } catch (Exception e) {
            log.error("An error occurs when checking if email exists :", e);
            res = ApiResponse.ERROR("[error] an used email checking error occurs : " + e.getMessage());
        }

        return res;
    }


    /**
     * Processing user's logins
     * @param request
     * @param response
     * @param user
     * @return
     */
    @PostMapping("/login")
    public ApiResponse<String> loginUser(HttpServletRequest request, HttpServletResponse response
        , @RequestBody User user) {
        ApiResponse<String> res = new ApiResponse<>();

        List<FieldCondition> fconds = new ArrayList<>();
        List<SortCondition> sconds = new ArrayList<>();

        try {
            if (StringUtils.isEmpty(user.getPasswordHash()))
                throw new IllegalArgumentException("Password can't be empty");

            fconds.add(new FieldCondition("email", Operator.EQ, user.getEmail(), LogicType.AND));
//            fconds.add(new FieldCondition("passwordHash", Operator.EQ, user.getPasswordHash(), LogicType.AND));

            // getting raw ps -> hash it and comparing to ps in db.
            // 클라이언트단에서는 비번을 평문으로 받음.
            // 클라이언트단에서 평문비번이 이미 노출되었다면 클라이언트단에서 해시된 비번도 보안상 의미 없음.
            User result = this.userService.getUser(fconds, sconds);
            String currentPassword = result.getPasswordHash();

            ShaUtil shaUtil = new ShaUtil();
//            String inputPassword = shaUtil.getHash(user.getPasswordHash().getBytes(), result.getSalt());
            String inputPassword = passwordEncoder.encode(user.getPasswordHash());
            if (!currentPassword.equals(inputPassword)) {
                return ApiResponse.ERROR("Incorrect password! Please check it again");
            }

            res = ApiResponse.SUCCESS("login success");
        } catch (Exception e) {
            res = ApiResponse.ERROR("[error] an login error occurs : " + e.getMessage());
            log.error("An error occurs when checking if email exists :", e);
        }

        return res;
    }


    /**
     * processing sending and saving verification code
     * @param userRequest
     * @return
     */
    @PostMapping("/send-verification")
    public ApiResponse<String> sendVerificationEmail(@RequestBody UserDto.Request userRequest) {
        ApiResponse<String> res = new ApiResponse<>();
        try {
            String email = userRequest.email();
            // Check if email exists
            List<FieldCondition> fconds = new ArrayList<>();
            fconds.add(new FieldCondition("email", Operator.EQ, email, LogicType.AND));

            if (!this.userService.exist(fconds)) {
                return ApiResponse.ERROR("Email not found in our system.");
            }

            // sending verification digit ex) 123456
            int randomNum = ((int)(Math.random() * 1000000));
            String randomDigits = String.format("%06d", randomNum); // 6자리 미만일시 0으로 채움
            this.userService.sendEmail(email, "budgetTool verification", randomDigits);

            // setting verification code 90's
            this.redisService.setSingleData("verification_opt" + email, randomDigits, Duration.ofSeconds(90));

            res = ApiResponse.SUCCESS("Verification email is successfully sent.");
        } catch (Exception e) {
            log.error("An error occurs when checking if email exists :", e);
            res = ApiResponse.ERROR("[error] an used email checking error occurs : " + e.getMessage());
        }

        return res;
    }


    /**
     * Verify opt code user has input.
     * After verification, signing up will be completed.
     * @param userRequest
     * @return
     */
    @PostMapping("/confirm-verification-code")
    public ApiResponse<String> confirmVerificationCode (@RequestBody(required = true) UserDto.Request userRequest) {
        ApiResponse<String> res = new ApiResponse<>();
        try{
            String inputOtp = userRequest.otp();
            String registeredOtp = this.redisService.getSingleData("verification_opt" + userRequest.email());

            if (registeredOtp == null || !registeredOtp.equals(inputOtp)) {
                log.info("The inputOpt is not matched with the registered one or expired.");
                return ApiResponse.ERROR("Wrong or expired opt. Please check again.");
            }

            User targetUser = userRequest.toEntity(this.passwordEncoder.encode(userRequest.passwordHash()));
            User newUser = this.userService.addUser(targetUser);

            //TODO set the sign-up success response!
            res = ApiResponse.SUCCESS("");

        } catch (Exception e) {
            log.error("An error occurs when confirming verification code :", e);
            res = ApiResponse.ERROR("[error] confirming verification code : " + e.getMessage());
        }

        return res;
    }

    /**
     * Send password reset verification code
     * @param userRequest
     * @return
     */
    @PostMapping("/forgot-password/send-code")
    public ApiResponse<String> sendPasswordResetCode(@RequestBody(required = true) UserDto.Request userRequest) {
        ApiResponse<String> res = new ApiResponse<>();
        try {
            String email = userRequest.email();

            // Check if email exists
            List<FieldCondition> fconds = new ArrayList<>();
            fconds.add(new FieldCondition("email", Operator.EQ, email, LogicType.AND));

            if (!this.userService.exist(fconds)) {
                return ApiResponse.ERROR("Email not found in our system.");
            }

            // Generate 6-digit verification code
            String resetCode = String.format("%06d", (int)(Math.random() * 1000000));
            
            // Send email
            this.userService.sendEmail(email, "Password Reset Verification", 
                "Your password reset code is: " + resetCode + "\n\nThis code will expire in 15 minutes.");

            // Store code in Redis with 15 minutes expiration
            this.redisService.setSingleData("PasswordReset_" + email, resetCode, Duration.ofMinutes(15));

            res = ApiResponse.SUCCESS("Password reset code has been sent to your email.");
        } catch (Exception e) {
            log.error("An error occurs when sending password reset code:", e);
            res = ApiResponse.ERROR("[error] Failed to send password reset code: " + e.getMessage());
        }

        return res;
    }

    /**
     * Verify password reset code
     * @param userRequest
     * @return
     */
    @PostMapping("/forgot-password/verify-code")
    public ApiResponse<String> verifyPasswordResetCode(@RequestBody UserDto.Request userRequest) {
        ApiResponse<String> res = new ApiResponse<>();
        try {
            String email = userRequest.email();
            String code = userRequest.otp();
            String storedCode = this.redisService.getSingleData("PasswordReset_" + email);

            if (storedCode == null || !storedCode.equals(code)) {
                log.info("Password reset code verification failed for email: " + email);
                return ApiResponse.ERROR("Invalid or expired verification code.");
            }

            // Generate a temporary token for password reset
            String resetToken = java.util.UUID.randomUUID().toString();
            this.redisService.setSingleData("ResetToken_" + email, resetToken, Duration.ofMinutes(15));

            res = ApiResponse.SUCCESS(resetToken);

        } catch (Exception e) {
            log.error("An error occurs when verifying password reset code:", e);
            res = ApiResponse.ERROR("[error] Verification failed: " + e.getMessage());
        }

        return res;
    }

    /**
     * Reset password with verification token
     * @param verification
     * @return
     */
    @PostMapping("/forgot-password/reset")
    public ApiResponse<String> resetPassword(@RequestBody UserDto.verification verification) {
        ApiResponse<String> res = new ApiResponse<>();
        try {
            String email = verification.email();
            String newPassword = verification.newPassword();
            String resetToken = verification.resetToken();

            // Verify reset token
            String storedToken = this.redisService.getSingleData("ResetToken_" + email);
            if (storedToken == null || !storedToken.equals(resetToken)) {
                return ApiResponse.ERROR("Invalid or expired reset token.");
            }

            // Get user
            List<FieldCondition> fconds = new ArrayList<>();
            fconds.add(new FieldCondition("email", Operator.EQ, email, LogicType.AND));
            User user = this.userService.getUser(fconds, null);

            if (user == null)
                return ApiResponse.ERROR("User not found.");

            // Update password
            user.setPasswordHash(this.passwordEncoder.encode(newPassword));
            this.userService.updateUser(user);

            // Clean up Redis tokens
            this.redisService.deleteSingleData("PasswordReset_" + email);
            this.redisService.deleteSingleData("ResetToken_" + email);

            res = ApiResponse.SUCCESS("Password has been reset successfully.");

        } catch (Exception e) {
            log.error("An error occurs when resetting password:", e);
            res = ApiResponse.ERROR("[error] Failed to reset password: " + e.getMessage());
        }

        return res;
    }
}
