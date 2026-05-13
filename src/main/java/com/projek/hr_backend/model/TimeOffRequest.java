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
@Table(name = "time_off_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeOffRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_off_type_id", nullable = false)
    private TimeOffType timeOffType;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(nullable = false)
    private Integer requested;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Column(name = "attachment_url")
    private String attachmentUrl;
    
    @Column(name = "attachment_name")
    private String attachmentName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TimeOffRequestStatus status = TimeOffRequestStatus.SUBMITTED;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
