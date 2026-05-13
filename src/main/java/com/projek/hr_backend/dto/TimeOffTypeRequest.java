package com.projek.hr_backend.dto;

import com.projek.hr_backend.model.TimeOffStatus;
import com.projek.hr_backend.model.TimeOffTypeEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeOffTypeRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotNull(message = "Type is required")
    private TimeOffTypeEnum type;
    
    @NotNull(message = "Max days per submission is required")
    @Min(value = 1, message = "Max days per submission must be > 0")
    private Integer maxDaysPerSubmission;
    
    @NotNull(message = "Status is required")
    private TimeOffStatus status;
}
