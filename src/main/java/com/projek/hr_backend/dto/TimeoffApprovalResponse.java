package com.projek.hr_backend.dto;

import com.projek.hr_backend.model.ApprovalStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TimeoffApprovalResponse {
    private Long id;
    private Long timeOffRequestId;
    private Long approverId;
    private String approverName;
    private ApprovalStatus status;
    private String notes;
    private LocalDateTime actionAt;
    private LocalDateTime createdAt;
}