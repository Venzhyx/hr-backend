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
public class EmployeeSalaryComponentRequest {

    @NotNull(message = "Salary component ID is required")
    private Long salaryComponentId;

    @NotNull(message = "Amount is required")
    @PositiveOrZero(message = "Amount cannot be negative")
    private BigDecimal amount;
}
