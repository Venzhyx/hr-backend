package com.projek.hr_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_salary_components")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSalaryComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_salary_id", nullable = false)
    private EmployeeSalary employeeSalary;

    // EAGER agar tidak LazyInitializationException saat mapping di service
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_component_id", nullable = false)
    private SalaryComponent salaryComponent;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
