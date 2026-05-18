package com.projek.hr_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayrollSettingRequest {

    @NotNull(message = "Absent deduction per day is required")
    @Positive(message = "Absent deduction per day must be greater than 0")
    private BigDecimal absentDeductionPerDay;

    @NotNull(message = "Late deduction per day is required")
    @Positive(message = "Late deduction per day must be greater than 0")
    private BigDecimal lateDeductionPerDay;
}
