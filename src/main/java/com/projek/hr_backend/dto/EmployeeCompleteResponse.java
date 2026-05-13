package com.projek.hr_backend.dto;

import com.projek.hr_backend.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCompleteResponse {
    
    // Employee Basic Info
    private Long id;
    private String name;
    private String jobTitle;
    private String workEmail;
    private String workPhone;
    private String workMobile;
    private LocalDate joinDate;
    private String photo;
    private Long companyId;
    private String companyName;
    private Long departmentId;
    private String departmentName;
    private Long managerId;
    private String managerName;
    private Long coachId;
    private String coachName;
    
    // Private Contact
    private String privateAddress;
    private String privateEmail;
    private String privatePhone;
    private String npwpId;
    private Double homeToWorkDistance;

    // WFH Location
    private String homeAddress;
    private Double homeLatitude;
    private Double homeLongitude;
    
    private List<EmployeeBankResponse> banks;
    private List<EmployeeInsuranceResponse> insurances;
    
    // Citizenship
    private String nationality;
    private String identificationNumber;
    private String passportNumber;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String placeOfBirth;
    private String countryOfBirth;
    private String ktpDocument;
    private String passportDocument;
    private String familyCardDocument;
    
    // Emergency
    private String emergencyContactName;
    private String emergencyContactPhone;
    
    // Education
    private CertificateLevel certificateLevel;
    private String fieldOfStudy;
    private String school;
    private String certificateDocument;
    private String transcriptDocument;
    
    // Family Status
    private MaritalStatus maritalStatus;
    private Integer numberOfDependentChildren;
    private String marriageCertificateDocument;
    private String childCertificateDocument;
    
    // Documents
    private String idCardCopy;
    private String familyCardCopy;
    private String drivingLicenseCopy;
    private String assuranceCardCopy;
    private String npwpCardCopy;
    
    // Settings
    private EmployeeStatus status;
    private EmployeeType employeeType;
    private String relatedUser;
    private String relatedUserName;
    private Double monthlyCost;
    private String employeeIdentificationNumber;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
