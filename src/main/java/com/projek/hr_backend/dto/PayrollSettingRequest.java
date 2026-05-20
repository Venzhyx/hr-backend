package com.projek.hr_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayrollSettingRequest {

    @NotNull(message = "Absent deduction per day is required")
    @PositiveOrZero(message = "Absent deduction per day must be 0 or greater")
    private BigDecimal absentDeductionPerDay;

    @NotNull(message = "Late deduction per day is required")
    @PositiveOrZero(message = "Late deduction per day must be 0 or greater")
    private BigDecimal lateDeductionPerDay;

    @NotNull(message = "Overtime rate per occurrence is required")
    @PositiveOrZero(message = "Overtime rate per occurrence must be 0 or greater")
    private BigDecimal overtimeRatePerOccurrence;

    @NotNull(message = "Overtime rate per hour is required")
    @PositiveOrZero(message = "Overtime rate per hour must be 0 or greater")
    private BigDecimal overtimeRatePerHour;
}