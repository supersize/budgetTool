package com.example.budgetTool.controller.rest;

import com.example.budgetTool.model.dto.ApiResponse;
import com.example.budgetTool.model.dto.DashboardDto;
import com.example.budgetTool.model.entity.User;
import com.example.budgetTool.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName    : com.example.budgetTool.controller.rest
 * author         : kimjaehyeong
 * date           : 12/08/25
 * description    : Dashboard REST API Controller
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/08/25        kimjaehyeong       created
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/Dashboard")
public class DashboardRestController {

    private final DashboardService dashboardService;

    /**
     * Get dashboard summary for current user
     * @param currentUser Current authenticated user
     * @return API response with dashboard data
     */
    @GetMapping
    public ApiResponse<DashboardDto.DashboardSummary> getDashboard(@AuthenticationPrincipal User currentUser) {
        ApiResponse<DashboardDto.DashboardSummary> res = new ApiResponse<>();
        try {
            DashboardDto.DashboardSummary summary = dashboardService.getDashboardSummary(currentUser.getId());
            res = ApiResponse.SUCCESS(summary);
        } catch (Exception e) {
            log.error("An error occurs when getting dashboard:", e);
            res = ApiResponse.ERROR("[error] Failed to get dashboard: " + e.getMessage());
        }

        return res;
    }
}
