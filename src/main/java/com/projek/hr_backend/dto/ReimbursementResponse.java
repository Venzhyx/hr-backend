package com.projek.hr_backend.dto;

import com.projek.hr_backend.model.ReimbursementStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReimbursementResponse {
    private Long id;
    private String title;
    private LocalDate expenseDate;
    private String category;
    private BigDecimal total;
    private Long employeeId;
    private String employeeName;
    private Long companyId;
    private String companyName;
    private String paidBy;
    private String notes;
    private String receiptFile;
    private ReimbursementStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
