package com.projek.hr_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.projek.hr_backend.model.ApprovalStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCorrectionResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    // ❌ REMOVE THIS LINE - causes error!
    // private String employeeCode;  
    private LocalDate date;
    private LocalDateTime newCheckIn;
    private LocalDateTime newCheckOut;
    private String type;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ApprovalDetail> approvals;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApprovalDetail {
        private Long id;
        private Long approverId;
        private String approverName;  // ✅ ADD THIS FIELD
        private Integer sequence;
        private ApprovalStatus status;
        private String notes;
        private LocalDateTime approvedAt;
    }
}