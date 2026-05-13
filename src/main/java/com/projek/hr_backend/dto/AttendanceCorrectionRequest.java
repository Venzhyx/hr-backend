package com.projek.hr_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCorrectionRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private LocalDateTime newCheckIn;
    private LocalDateTime newCheckOut;

    @NotNull(message = "Type is required")
    private String type;

    private String description;
}
