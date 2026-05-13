package com.projek.hr_backend.dto;

import com.projek.hr_backend.model.PayslipComponentType;
import com.projek.hr_backend.model.PayslipStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayslipResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;

    // Info periode
    private Long periodId;
    private String periodLabel;
    private Integer month;
    private Integer year;

    // Status per-payslip — independen dari PayrollPeriod
    private PayslipStatus status;

    // Komponen gaji — snapshot immutable
    private BigDecimal basicSalary;
    private BigDecimal overtimePay;
    private BigDecimal totalEarning;
    private BigDecimal totalDeduction;
    private BigDecimal netSalary;

    // Ringkasan overtime & attendance
    private Double  totalOvertimeHours;
    private Integer totalAbsent;
    private Integer totalLate;

    private LocalDateTime generatedAt;

    // Detail komponen baris per baris
    private List<ComponentItem> components;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentItem {
        private Long id;
        private String componentName;
        private PayslipComponentType type;
        private BigDecimal amount;
    }
}
