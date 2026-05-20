package com.projek.hr_backend.dto;

import com.projek.hr_backend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    @Builder.Default
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private Role role;
    private Long employeeId;
}
