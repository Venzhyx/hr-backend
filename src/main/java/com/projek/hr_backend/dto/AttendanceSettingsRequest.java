package com.projek.hr_backend.dto;

import com.projek.hr_backend.model.AttendanceMode;
import com.projek.hr_backend.model.ExtraHoursValidation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSettingsRequest {
    
    @NotNull(message = "Tolerance time is required")
    @Min(value = 0, message = "Tolerance time must be >= 0")
    private Integer toleranceTimeInFavorOfEmployee;
    
    @NotNull(message = "Extra hours validation is required")
    private ExtraHoursValidation extraHoursValidation;

    private LocalTime checkInTime;
    private LocalTime checkOutTime;

    private Double officeLatitude;
    private Double officeLongitude;
    private Integer wfoRadius;
    private Integer wfhRadius;

    /** ONLINE = absen via app, OFFLINE = absen via mesin/Excel. */
    private AttendanceMode mode;
}
