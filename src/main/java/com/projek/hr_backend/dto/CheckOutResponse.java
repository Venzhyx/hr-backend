package com.projek.hr_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutResponse {
    private String status;
    private Long employeeId;
    private String employeeName;
    private String checkOutTime;
    private String attendanceStatus;
    private String message;
}
