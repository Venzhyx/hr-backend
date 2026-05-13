package com.projek.hr_backend.dto;

import com.projek.hr_backend.model.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReimbursementApprovalResponse {
    private Long id;
    private Long reimbursementId;
    private String reimbursementTitle;
    private Long approverId;
    private String approverName;
    private ApprovalStatus status;
    private String notes;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
}
