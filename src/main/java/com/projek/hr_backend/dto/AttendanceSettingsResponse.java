package com.projek.hr_backend.dto;

import com.projek.hr_backend.model.AttendanceMode;
import com.projek.hr_backend.model.ExtraHoursValidation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSettingsResponse {
    private Long id;
    private Integer toleranceTimeInFavorOfEmployee;
    private ExtraHoursValidation extraHoursValidation;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Double officeLatitude;
    private Double officeLongitude;
    private Integer wfoRadius;
    private Integer wfhRadius;
    /** ONLINE = absen via app, OFFLINE = absen via mesin/Excel. */
    private AttendanceMode mode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
