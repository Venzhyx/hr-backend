package com.projek.hr_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "attendance_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tolerance_time_in_favor_of_employee", nullable = false)
    private Integer toleranceTimeInFavorOfEmployee = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "extra_hours_validation", nullable = false)
    private ExtraHoursValidation extraHoursValidation = ExtraHoursValidation.APPROVED_BY_MANAGER;

    @Column(name = "check_in_time")
    private LocalTime checkInTime = LocalTime.of(8, 0);

    @Column(name = "check_out_time")
    private LocalTime checkOutTime = LocalTime.of(17, 0);

    @Column(name = "office_latitude")
    private Double officeLatitude;

    @Column(name = "office_longitude")
    private Double officeLongitude;

    @Column(name = "wfo_radius")
    private Integer wfoRadius = 100;

    @Column(name = "wfh_radius")
    private Integer wfhRadius = 100;

    /**
     * Mode operasi absensi.
     * ONLINE  → karyawan absen via aplikasi (check-in/out aktif, scheduler skip).
     * OFFLINE → absensi dari mesin/Excel (check-in/out diblokir, scheduler aktif).
     * Default OFFLINE agar tidak breaking existing behavior.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false, length = 10)
    private AttendanceMode mode = AttendanceMode.OFFLINE;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
