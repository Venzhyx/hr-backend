package com.projek.hr_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDocument {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;
    
    @Column(columnDefinition = "TEXT")
    private String idCardCopy;
    
    @Column(columnDefinition = "TEXT")
    private String familyCardCopy;
    
    @Column(columnDefinition = "TEXT")
    private String drivingLicenseCopy;
    
    @Column(columnDefinition = "TEXT")
    private String assuranceCardCopy;
    
    @Column(columnDefinition = "TEXT")
    private String npwpCardCopy;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
