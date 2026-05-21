package com.projek.hr_backend.dto;

import com.projek.hr_backend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private Role role;
    private Long employeeId;
    private Boolean isActive;
}
