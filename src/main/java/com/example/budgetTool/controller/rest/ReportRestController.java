package com.example.budgetTool.controller.rest;

import com.example.budgetTool.model.dto.ApiResponse;
import com.example.budgetTool.model.dto.ReportDto;
import com.example.budgetTool.model.entity.User;
import com.example.budgetTool.service.ReportService;
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
 * description    : Report REST API Controller
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/08/25        kimjaehyeong       created
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/Reports")
public class ReportRestController {

    private final ReportService reportService;

    /**
     * Get report for current user
     * @param currentUser Current authenticated user
     * @return API response with report data
     */
    @GetMapping
    public ApiResponse<ReportDto.ReportResponse> getReport(@AuthenticationPrincipal User currentUser) {
        ApiResponse<ReportDto.ReportResponse> res = new ApiResponse<>();
        try {
            ReportDto.ReportResponse report = reportService.generateReport(currentUser.getId());
            res = ApiResponse.SUCCESS(report);
        } catch (Exception e) {
            log.error("An error occurs when generating report:", e);
            res = ApiResponse.ERROR("[error] Failed to generate report: " + e.getMessage());
        }

        return res;
    }
}
