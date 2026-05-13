package com.projek.hr_backend.dto;

import com.projek.hr_backend.model.TimeOffRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeOffRequestResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long timeOffTypeId;
    private String timeOffTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer requested;
    private String reason;
    private String attachmentUrl;
    private String attachmentName;
    private TimeOffRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
