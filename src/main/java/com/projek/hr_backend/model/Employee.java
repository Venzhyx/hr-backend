package com.projek.hr_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String jobTitle;
    
    @Column(nullable = false)
    private String workEmail;
    
    @Column(nullable = false)
    private String workPhone;
    
    private String workMobile;
    
    @Column(nullable = false)
    private LocalDate joinDate;
    
    private String photo;
    
    @Column(name = "home_latitude")
    private Double homeLatitude;

    @Column(name = "home_longitude")
    private Double homeLongitude;

    @Column(name = "home_address")
    private String homeAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id")
    private Employee coach;
    
    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    private EmployeePrivateInfo privateInfo;
    
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeeBank> banks = new ArrayList<>();
    
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeeInsurance> insurances = new ArrayList<>();
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
