package com.projek.hr_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonIgnoreProperties({
        "company", "department", "manager",
        "workEmail", "workPhone", "workMobile",
        "joinDate", "photo", "hibernateLazyInitializer", "handler"
    })
    private Employee employee;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "check_in")
    private LocalDateTime checkIn;

    @Column(name = "check_out")
    private LocalDateTime checkOut;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "employee_code")
    private String employeeCode;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "photo_path")
    private String photoPath;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "check_out_photo_path")
    private String checkOutPhotoPath;

    @Column(name = "check_out_latitude")
    private Double checkOutLatitude;

    @Column(name = "check_out_longitude")
    private Double checkOutLongitude;

    @Column(name = "is_location_validated")
    private Boolean isLocationValidated;

    @Column(name = "is_suspicious")
    private Boolean isSuspicious = false;

    @Column(name = "suspicious_reason")
    private String suspiciousReason;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "ip_latitude")
    private Double ipLatitude;

    @Column(name = "ip_longitude")
    private Double ipLongitude;

    @Column(name = "gps_accuracy")
    private Double gpsAccuracy;

    @Column(name = "device_info", columnDefinition = "TEXT")
    private String deviceInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_type", length = 10)
    private AttendanceType attendanceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", length = 10)
    private AttendanceSource source = AttendanceSource.EXCEL;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}