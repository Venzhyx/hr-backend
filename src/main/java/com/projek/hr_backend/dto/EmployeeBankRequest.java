package com.projek.hr_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeBankRequest {
    
    @NotBlank(message = "Bank name is required")
    private String bankName;
    
    @NotBlank(message = "Account number is required")
    private String accountNumber;
    
    @NotBlank(message = "Account holder is required")
    private String accountHolder;
}
