package com.projek.hr_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSalaryRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Basic salary is required")
    @PositiveOrZero(message = "Basic salary cannot be negative")
    private BigDecimal basicSalary;

    @NotNull(message = "Effective date is required")
    private LocalDate effectiveDate;
}
