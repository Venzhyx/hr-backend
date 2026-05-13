package com.projek.hr_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationValidationResponse {
    private boolean isValid;
    private double distance;
    private double radius;
    private String message;
}
