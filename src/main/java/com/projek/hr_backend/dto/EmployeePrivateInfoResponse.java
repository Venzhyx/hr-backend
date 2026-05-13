package com.projek.hr_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePrivateInfoResponse {
    
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String privateAddress;
    private String privateEmail;
    private String privatePhone;
    private String bankName;
    private String accountNumber;
    private Double homeToWorkDistance;
    private String bpjsId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
