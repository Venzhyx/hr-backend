package com.projek.hr_backend.dto;

import com.projek.hr_backend.model.SalaryComponentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSalaryResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private BigDecimal basicSalary;
    private LocalDate effectiveDate;
    private Boolean isActive;
    private List<ComponentDetail> components;
    private BigDecimal totalEarning;
    private BigDecimal totalDeduction;
    private BigDecimal netSalaryEstimate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentDetail {
        private Long id;
        private Long salaryComponentId;
        private String componentName;
        private SalaryComponentType componentType;
        private BigDecimal amount;
    }
}
