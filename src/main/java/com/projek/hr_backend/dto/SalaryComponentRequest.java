package com.projek.hr_backend.dto;

import com.projek.hr_backend.model.SalaryCalculationType;
import com.projek.hr_backend.model.SalaryComponentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryComponentRequest {

    /**
     * Kode unik komponen. Hanya huruf kapital, angka, dan underscore.
     * Contoh: BASIC, MEAL, TRANSPORT, LATE_DEDUCTION
     */
    @NotBlank(message = "Code is required")
    @Size(max = 50, message = "Code must not exceed 50 characters")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "Code must be uppercase letters, numbers, or underscores only")
    private String code;

    @NotBlank(message = "Name is required")
    private String name;

    /** Deskripsi opsional untuk menjelaskan komponen ini. */
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @NotNull(message = "Type is required")
    private SalaryComponentType type;

    @NotNull(message = "Calculation type is required")
    private SalaryCalculationType calculationType;

    private Boolean isActive = true;
}
