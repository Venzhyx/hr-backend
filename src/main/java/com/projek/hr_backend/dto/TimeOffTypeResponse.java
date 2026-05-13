package com.projek.hr_backend.dto;

import com.projek.hr_backend.model.TimeOffStatus;
import com.projek.hr_backend.model.TimeOffTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeOffTypeResponse {
    private Long id;
    private String name;
    private TimeOffTypeEnum type;
    private Integer maxDaysPerSubmission;
    private TimeOffStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
