package com.example.budgetTool.controller.rest;

import com.example.budgetTool.model.dto.ApiResponse;
import com.example.budgetTool.model.dto.UserDto;
import com.example.budgetTool.model.entity.User;
import com.example.budgetTool.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * packageName    : com.example.budgetTool.controller.rest
 * author         : kimjaehyeong
 * date           : 12/08/25
 * description    : Settings REST API Controller
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/08/25        kimjaehyeong       created
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/Settings")
public class SettingsRestController {

    private final UserService userService;

    /**
     * Get current user profile
     * @param currentUser Current authenticated user
     * @return API response with user data
     */
    @GetMapping("/profile")
    public ApiResponse<UserDto.Response> getProfile(@AuthenticationPrincipal User currentUser) {
        ApiResponse<UserDto.Response> res = new ApiResponse<>();
        try {
            UserDto.Response response = UserDto.Response.from(UserDto.from(currentUser));
            res = ApiResponse.SUCCESS(response);
        } catch (Exception e) {
            log.error("An error occurs when getting profile:", e);
            res = ApiResponse.ERROR("[error] Failed to get profile: " + e.getMessage());
        }
        return res;
    }

    /**
     * Update user profile
     * @param request Update request
     * @param currentUser Current authenticated user
     * @return API response with updated user data
     */
    @PutMapping("/profile")
    public ApiResponse<UserDto.Response> updateProfile(
            @Validated @RequestBody UserDto.UpdateRequest request,
            @AuthenticationPrincipal User currentUser) {
        ApiResponse<UserDto.Response> res = new ApiResponse<>();
        try {
            User updatedUser = userService.updateUserProfile(currentUser, request);
            UserDto.Response response = UserDto.Response.from(UserDto.from(updatedUser));
            res = ApiResponse.SUCCESS(response);
            
            log.info("Profile updated for user: {}", currentUser.getEmail());
        } catch (IllegalArgumentException e) {
            log.error("Profile update validation error:", e);
            res = ApiResponse.ERROR(e.getMessage());
        } catch (Exception e) {
            log.error("An error occurs when updating profile:", e);
            res = ApiResponse.ERROR("[error] Failed to update profile: " + e.getMessage());
        }
        return res;
    }

    /**
     * Change user password
     * @param request Change password request
     * @param currentUser Current authenticated user
     * @return API response
     */
    @PutMapping("/password")
    public ApiResponse<String> changePassword(
            @Validated @RequestBody UserDto.ChangePasswordRequest request,
            @AuthenticationPrincipal User currentUser) {
        ApiResponse<String> res = new ApiResponse<>();
        try {
            // Validate passwords match
            if (!request.newPassword().equals(request.confirmPassword())) {
                return ApiResponse.ERROR("New passwords do not match");
            }

            // Validate password strength (at least 8 characters)
            if (request.newPassword().length() < 8) {
                return ApiResponse.ERROR("Password must be at least 8 characters long");
            }

            userService.changePassword(currentUser, request.currentPassword(), request.newPassword());
            res = ApiResponse.SUCCESS("Password changed successfully");
            
            log.info("Password changed for user: {}", currentUser.getEmail());
        } catch (IllegalArgumentException e) {
            log.error("Password change validation error:", e);
            res = ApiResponse.ERROR(e.getMessage());
        } catch (Exception e) {
            log.error("An error occurs when changing password:", e);
            res = ApiResponse.ERROR("[error] Failed to change password: " + e.getMessage());
        }
        return res;
    }
}
