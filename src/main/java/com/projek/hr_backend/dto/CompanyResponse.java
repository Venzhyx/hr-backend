package com.projek.hr_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponse {
    
    private Long id;
    private String logo;
    private String companyName;
    private String address;
    private String country;
    private String city;
    private String zip;
    private String companyId;
    private String phone;
    private String email;
    private String website;
    private Double latitude;
    private Double longitude;
    private String formattedAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
