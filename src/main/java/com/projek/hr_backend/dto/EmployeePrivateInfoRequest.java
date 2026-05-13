package com.projek.hr_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePrivateInfoRequest {
    
    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    private String privateAddress;
    private String privateEmail;
    private String privatePhone;
    private String bankName;
    private String accountNumber;
    private Double homeToWorkDistance;
    private String bpjsId;
}
