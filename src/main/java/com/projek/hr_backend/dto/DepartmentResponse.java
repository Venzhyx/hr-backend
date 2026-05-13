package com.projek.hr_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponse {
    
    private Long id;
    private String departmentName;
    private Long parentDepartmentId;
    private String parentDepartmentName;
    private Long companyId;
    private String companyName;
    private Long managerId;
    private String managerName;
    private List<DepartmentResponse> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
