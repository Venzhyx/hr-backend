package com.projek.hr_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReimbursementRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    @NotNull(message = "Total is required")
    @Positive(message = "Total must be positive")
    private BigDecimal total;
    
    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    private Long companyId;
    private String paidBy;
    private String notes;
    private String receiptFile;
}
