package com.projek.hr_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String departmentName;
    
    @ManyToOne
    @JoinColumn(name = "parent_department_id")
    private Department parentDepartment;
    
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employee manager;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
