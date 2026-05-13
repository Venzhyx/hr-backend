package com.projek.hr_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee_banks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeBank {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String bankName;
    
    @Column(nullable = false)
    private String accountNumber;
    
    private String accountHolder;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
