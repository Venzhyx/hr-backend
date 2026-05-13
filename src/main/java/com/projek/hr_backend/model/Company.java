package com.projek.hr_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String logo;
    
    @Column(nullable = false)
    private String companyName;
    
    private String address;
    
    private String country;
    
    private String city;
    
    private String zip;
    
    private String companyId;
    
    private String phone;
    
    private String email;
    
    private String website;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(length = 500)
    private String formattedAddress;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
