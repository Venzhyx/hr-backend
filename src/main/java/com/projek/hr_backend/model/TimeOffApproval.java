package com.projek.hr_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "time_off_approvals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeOffApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_off_request_id", nullable = false)
    private TimeOffRequest timeOffRequest;

    /** Raw ID — konsisten dengan AttendanceCorrectionApproval & OvertimeApproval. */
    @Column(name = "approver_id", nullable = false)
    private Long approverId;

    /** Urutan approval — dipakai frontend untuk processApprovalByLevel. */
    @Column(nullable = false)
    private Integer sequence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "action_at")
    private LocalDateTime actionAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
