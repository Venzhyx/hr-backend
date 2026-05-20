package com.projek.hr_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayrollSettingResponse {

    private BigDecimal absentDeductionPerDay;
    private BigDecimal lateDeductionPerDay;
    private BigDecimal overtimeRatePerOccurrence;
    private BigDecimal overtimeRatePerHour;
    private LocalDateTime updatedAt;
}
