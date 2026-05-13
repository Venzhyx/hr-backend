package com.projek.hr_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "time_off_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeOffType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeOffTypeEnum type;
    
    @Column(nullable = false)
    private Integer maxDaysPerSubmission;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeOffStatus status = TimeOffStatus.ACTIVE;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
