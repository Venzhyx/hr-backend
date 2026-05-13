    package com.projek.hr_backend.dto;

    import com.projek.hr_backend.model.ApprovalStatus;
    import com.projek.hr_backend.model.OvertimeType;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.util.List;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class OvertimeResponse {
        private Long id;
        private Long employeeId;
        private String employeeName;
        private LocalDate date;
        private OvertimeType type;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Double totalHours;
        private String description;
        private ApprovalStatus status;
        private List<ApprovalDetail> approvals;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ApprovalDetail {
            private Long id;
            private Long approverId;
            private String approverName;
            private Integer sequence;
            private ApprovalStatus status;
            private String notes;
            private LocalDateTime approvedAt;
        }
    }
