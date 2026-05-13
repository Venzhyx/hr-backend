package com.projek.hr_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reimbursements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reimbursement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;
    
    @Column(nullable = false)
    private String category;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @Column(name = "paid_by", length = 20)
    private String paidBy;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "receipt_file")
    private String receiptFile;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReimbursementStatus status = ReimbursementStatus.SUBMITTED;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
