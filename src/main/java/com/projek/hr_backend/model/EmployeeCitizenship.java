package com.projek.hr_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_citizenship")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCitizenship {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;
    
    private String nationality;
    
    private String identificationNo;
    
    private String passportNo;
    
    private String familyCardNo;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    private LocalDate dateOfBirth;
    
    private String placeOfBirth;
    
    private String countryOfBirth;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
