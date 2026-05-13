package com.projek.hr_backend.controller;

import com.projek.hr_backend.dto.ApiResponse;
import com.projek.hr_backend.dto.DashboardSummaryResponse;
import com.projek.hr_backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboardSummary() {
        DashboardSummaryResponse response = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(ApiResponse.success("Dashboard summary retrieved successfully", response));
    }
}
