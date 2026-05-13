package com.projek.hr_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;
    
    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;
    
    @Enumerated(EnumType.STRING)
    private EmployeeType employeeType;
    
    private String relatedUser;
    
    private Double monthlyCost;
    
    private String employeeIdentificationNumber;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
