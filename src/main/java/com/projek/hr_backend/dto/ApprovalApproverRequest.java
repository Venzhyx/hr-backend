package com.projek.hr_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalApproverRequest {
    
    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    private Boolean isRequired = false;
    
    @NotNull(message = "Approval order is required")
    private Integer approvalOrder;
    
    @NotNull(message = "Minimum approval is required")
    @Min(value = 1, message = "Minimum approval must be at least 1")
    private Integer minimumApproval;
}
