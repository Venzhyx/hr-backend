package com.projek.hr_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInResponse {
    private String status;
    private Long employeeId;
    private String employeeName;
    private String checkInTime;
    private String attendanceType;
    private Double latitude;
    private Double longitude;
    private String attendanceStatus;
    private Boolean isLocationValidated;
    private Double distance;
    private Double radius;
    private String locationMessage;
    private Boolean isSuspicious;
    private String suspiciousReason;
    private String photoPath; // ✅ TAMBAH: agar frontend bisa tahu URL foto
}
