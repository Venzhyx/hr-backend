package com.projek.hr_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payslip bersifat IMMUTABLE setelah dibuat.
 * Semua kolom kalkulasi ditandai updatable=false.
 * Jika ada koreksi, buat payroll run baru — jangan update payslip lama.
 */
@Entity
@Table(
    name = "payslips",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_payslip_period_employee",
            columnNames = {"payroll_period_id", "employee_id"})
    },
    indexes = {
        @Index(name = "idx_payslip_employee_id",       columnList = "employee_id"),
        @Index(name = "idx_payslip_payroll_period_id", columnList = "payroll_period_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_period_id", nullable = false, updatable = false)
    private PayrollPeriod payrollPeriod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, updatable = false)
    private Employee employee;

    // ─── Snapshot kalkulasi — immutable setelah dibuat ───────────────────────

    @Column(name = "basic_salary", nullable = false, precision = 15, scale = 2, updatable = false)
    private BigDecimal basicSalary;

    @Column(name = "total_earning", nullable = false, precision = 15, scale = 2, updatable = false)
    private BigDecimal totalEarning;

    @Column(name = "total_deduction", nullable = false, precision = 15, scale = 2, updatable = false)
    private BigDecimal totalDeduction;

    @Column(name = "overtime_pay", nullable = false, precision = 15, scale = 2, updatable = false)
    private BigDecimal overtimePay;

    /** Total jam lembur APPROVED yang dihitung pada periode ini. */
    @Column(name = "total_overtime_hours", nullable = false, updatable = false)
    private Double totalOvertimeHours;

    @Column(name = "net_salary", nullable = false, precision = 15, scale = 2, updatable = false)
    private BigDecimal netSalary;

    // ─── Attendance summary — snapshot untuk laporan ─────────────────────────

    /** Jumlah hari ABSENT dalam periode ini. */
    @Column(name = "total_absent", nullable = false, updatable = false)
    private Integer totalAbsent;

    /** Jumlah hari LATE dalam periode ini. */
    @Column(name = "total_late", nullable = false, updatable = false)
    private Integer totalLate;

    @CreationTimestamp
    @Column(name = "generated_at", updatable = false)
    private LocalDateTime generatedAt;

    /**
     * Status per-payslip — independen dari PayrollPeriod.
     * Default DRAFT saat generate, bisa di-approve (FINALIZED) atau di-paid (PAID) per individu.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PayslipStatus status = PayslipStatus.DRAFT;
}
