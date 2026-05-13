package com.projek.hr_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRequest {
    
    @NotBlank(message = "Department name is required")
    private String departmentName;
    
    private Long parentDepartmentId;
    
    @NotNull(message = "Company is required")
    private Long companyId;
    
    private Long managerId;
}
