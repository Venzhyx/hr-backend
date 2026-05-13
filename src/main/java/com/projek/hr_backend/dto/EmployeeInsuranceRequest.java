package com.projek.hr_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeInsuranceRequest {
    
    @NotBlank(message = "Insurance type is required")
    private String type;
    
    @NotBlank(message = "Provider is required")
    private String provider;
    
    @NotBlank(message = "Policy number is required")
    private String policyNumber;
}
