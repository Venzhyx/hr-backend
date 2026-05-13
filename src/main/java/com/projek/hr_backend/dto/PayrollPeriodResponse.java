package com.projek.hr_backend.dto;

import com.projek.hr_backend.model.PayrollPeriodStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayrollPeriodResponse {

    private Long id;
    private Integer month;
    private Integer year;
    private String periodLabel;       // contoh: "April 2026"
    private LocalDate startDate;
    private LocalDate endDate;
    private PayrollPeriodStatus status;

    // ─── Run summary — info hasil payroll run ─────────────────────────────────
    /** Jumlah payslip berhasil di-generate. */
    private int successCount;

    /** Jumlah employee yang di-skip karena tidak punya salary aktif. */
    private int skippedCount;

    /** Jumlah employee yang gagal diproses karena error tak terduga. */
    private int failedCount;

    /** Total employee yang diproses (success + skipped + failed). */
    private int totalEmployees;

    // Tetap ada untuk backward compat — sama dengan successCount
    private int totalPayslips;

    private List<PayslipResponse> payslips;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
