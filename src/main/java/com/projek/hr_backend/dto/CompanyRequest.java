package com.projek.hr_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequest {
    
    private String logo;
    
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    private String address;
    private String country;
    private String city;
    private String zip;
    private String companyId;
    private String phone;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String website;
    private Double latitude;
    private Double longitude;
    private String formattedAddress;
}
