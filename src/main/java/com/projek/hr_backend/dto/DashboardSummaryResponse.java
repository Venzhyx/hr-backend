package com.projek.hr_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    
    private Long totalEmployee;
    private Long totalFullTime;
    private Long totalPartTime;
    private Long totalContract;
    private Map<String, Long> monthlyJoinStatistic;
}
