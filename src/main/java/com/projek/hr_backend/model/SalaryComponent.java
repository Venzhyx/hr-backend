package com.projek.hr_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "salary_components")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Kode unik komponen, dipakai saat payroll run untuk identifikasi aman.
     * Contoh: BASIC, MEAL, TRANSPORT, LATE_DEDUCTION
     */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false)
    private String name;

    /** Deskripsi opsional untuk menjelaskan komponen ini. */
    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SalaryComponentType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "calculation_type", nullable = false, length = 20)
    private SalaryCalculationType calculationType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
