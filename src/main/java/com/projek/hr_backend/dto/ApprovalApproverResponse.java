package com.projek.hr_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalApproverResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Boolean isRequired;
    private Integer approvalOrder;
    private Integer minimumApproval;
    private LocalDateTime createdAt;
}
