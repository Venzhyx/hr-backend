package com.projek.hr_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_approvers", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalApprover {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @Column(name = "is_required")
    private Boolean isRequired = false;
    
    @Column(name = "approval_order", nullable = false)
    private Integer approvalOrder;
    
    @Column(name = "minimum_approval", nullable = false)
    private Integer minimumApproval = 1;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
