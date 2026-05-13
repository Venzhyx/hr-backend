package com.projek.hr_backend.dto;

import com.projek.hr_backend.model.SalaryCalculationType;
import com.projek.hr_backend.model.SalaryComponentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryComponentResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private SalaryComponentType type;
    private SalaryCalculationType calculationType;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
