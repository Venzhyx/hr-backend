package com.projek.hr_backend.controller;

import com.projek.hr_backend.dto.*;
import com.projek.hr_backend.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final SalaryComponentService      salaryComponentService;
    private final EmployeeSalaryService       employeeSalaryService;
    private final PayrollRunService           payrollRunService;
    private final PayslipService              payslipService;
    private final PayslipPdfService           payslipPdfService;
    private final PayrollReportExcelService   payrollReportExcelService;

    // ─── Salary Component ─────────────────────────────────────────────────────

    /** POST /api/payroll/components — Buat salary component baru */
    @PostMapping("/components")
    public ResponseEntity<ApiResponse<SalaryComponentResponse>> createComponent(
            @Valid @RequestBody SalaryComponentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Salary component created successfully",
                        salaryComponentService.create(request)));
    }

    /** GET /api/payroll/components — Semua komponen (?activeOnly=true untuk aktif saja) */
    @GetMapping("/components")
    public ResponseEntity<ApiResponse<List<SalaryComponentResponse>>> getAllComponents(
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        List<SalaryComponentResponse> data = activeOnly
                ? salaryComponentService.getAllActive()
                : salaryComponentService.getAll();
        return ResponseEntity.ok(ApiResponse.success("Salary components retrieved successfully", data));
    }

    /** PUT /api/payroll/components/{id} — Update salary component */
    @PutMapping("/components/{id}")
    public ResponseEntity<ApiResponse<SalaryComponentResponse>> updateComponent(
            @PathVariable Long id,
            @Valid @RequestBody SalaryComponentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Salary component updated successfully",
                salaryComponentService.update(id, request)));
    }

    /** DELETE /api/payroll/components/{id} — Deactivate salary component (soft delete) */
    @DeleteMapping("/components/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateComponent(@PathVariable Long id) {
        salaryComponentService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success("Salary component deactivated successfully", null));
    }

    // ─── Employee Salary ──────────────────────────────────────────────────────

    /** POST /api/payroll/employee-salary — Set gaji pokok employee */
    @PostMapping("/employee-salary")
    public ResponseEntity<ApiResponse<EmployeeSalaryResponse>> setEmployeeSalary(
            @Valid @RequestBody EmployeeSalaryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee salary set successfully",
                        employeeSalaryService.setBasicSalary(request)));
    }

    /** POST /api/payroll/employee-salary/{id}/components — Tambah komponen ke salary */
    @PostMapping("/employee-salary/{id}/components")
    public ResponseEntity<ApiResponse<EmployeeSalaryResponse>> addSalaryComponent(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeSalaryComponentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Salary component added successfully",
                        employeeSalaryService.addComponent(id, request)));
    }

    /** GET /api/payroll/employee-salary/{employeeId} — Detail salary aktif employee */
    @GetMapping("/employee-salary/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeSalaryResponse>> getEmployeeSalary(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(ApiResponse.success("Employee salary retrieved successfully",
                employeeSalaryService.getSalaryDetail(employeeId)));
    }

    /** GET /api/payroll/employee-salary/{employeeId}/history — Riwayat salary employee */
    @GetMapping("/employee-salary/{employeeId}/history")
    public ResponseEntity<ApiResponse<List<EmployeeSalaryResponse>>> getEmployeeSalaryHistory(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(ApiResponse.success("Employee salary history retrieved successfully",
                employeeSalaryService.getSalaryHistory(employeeId)));
    }

    // ─── Payroll Run ──────────────────────────────────────────────────────────

    /**
     * POST /api/payroll/run
     * Jalankan payroll untuk bulan dan tahun tertentu.
     */
    @PostMapping("/run")
    public ResponseEntity<ApiResponse<PayrollPeriodResponse>> runPayroll(
            @Valid @RequestBody PayrollRunRequest request) {
        PayrollPeriodResponse response = payrollRunService.runPayroll(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Payroll run completed. Generated " + response.getTotalPayslips()
                        + " payslip(s) for " + response.getPeriodLabel(), response));
    }

    /**
     * GET /api/payroll/runs
     * Ambil semua payroll period yang pernah di-run, diurutkan terbaru dulu.
     * Dipakai FE untuk menampilkan daftar payroll di IndexPayroll.
     */
    @GetMapping("/runs")
    public ResponseEntity<ApiResponse<List<PayrollPeriodResponse>>> getAllPayrollRuns() {
        List<PayrollPeriodResponse> response = payrollRunService.getAllRuns();
        return ResponseEntity.ok(ApiResponse.success("Payroll runs retrieved successfully", response));
    }

    /**
     * GET /api/payroll/runs/{periodId}
     * Ambil detail satu payroll period beserta semua payslip-nya.
     */
    @GetMapping("/runs/{periodId}")
    public ResponseEntity<ApiResponse<PayrollPeriodResponse>> getPayrollRunDetail(
            @PathVariable Long periodId) {
        PayrollPeriodResponse response = payrollRunService.getRunDetail(periodId);
        return ResponseEntity.ok(ApiResponse.success("Payroll run detail retrieved successfully", response));
    }

    // ─── Payslip ──────────────────────────────────────────────────────────────

    /** GET /api/payroll/payslips/{employeeId} — Semua payslip milik satu employee */
    @GetMapping("/payslips/{employeeId}")
    public ResponseEntity<ApiResponse<List<PayslipResponse>>> getPayslipsByEmployee(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(ApiResponse.success("Payslips retrieved successfully",
                payslipService.getPayslipsByEmployee(employeeId)));
    }

    /** GET /api/payroll/payslips/detail/{payslipId} — Detail satu payslip lengkap */
    @GetMapping("/payslips/detail/{payslipId}")
    public ResponseEntity<ApiResponse<PayslipResponse>> getPayslipDetail(
            @PathVariable Long payslipId) {
        return ResponseEntity.ok(ApiResponse.success("Payslip detail retrieved successfully",
                payslipService.getPayslipDetail(payslipId)));
    }

    /**
     * PATCH /api/payroll/payslips/{payslipId}/approve
     * Approve payslip — mengubah status period dari DRAFT → FINALIZED.
     * Hanya DRAFT yang boleh di-approve.
     */
    @PatchMapping("/payslips/{payslipId}/approve")
    public ResponseEntity<ApiResponse<PayslipResponse>> approvePayslip(
            @PathVariable Long payslipId) {
        return ResponseEntity.ok(ApiResponse.success("Payslip approved successfully",
                payslipService.approvePayslip(payslipId)));
    }

    /**
     * DELETE /api/payroll/payslips/{payslipId}
     * Hapus payslip — hanya boleh jika status period masih DRAFT.
     * Hard delete: payslip + semua komponennya dihapus permanen.
     */
    @DeleteMapping("/payslips/{payslipId}")
    public ResponseEntity<ApiResponse<Void>> deletePayslip(
            @PathVariable Long payslipId) {
        payslipService.deletePayslip(payslipId);
        return ResponseEntity.ok(ApiResponse.success("Payslip deleted successfully", null));
    }

    /**
     * PATCH /api/payroll/payslips/{payslipId}/paid
     * Mark payslip as paid — mengubah status period dari FINALIZED → PAID.
     * Hanya FINALIZED yang boleh di-mark as paid.
     */
    @PatchMapping("/payslips/{payslipId}/paid")
    public ResponseEntity<ApiResponse<PayslipResponse>> markAsPaid(
            @PathVariable Long payslipId) {
        return ResponseEntity.ok(ApiResponse.success("Payslip marked as paid successfully",
                payslipService.markAsPaid(payslipId)));
    }

    // ─── Export PDF ───────────────────────────────────────────────────────────

    /**
     * GET /api/payroll/payslips/{payslipId}/pdf
     * Export satu payslip sebagai PDF.
     */
    @GetMapping("/payslips/{payslipId}/pdf")
    public ResponseEntity<byte[]> exportPayslipPdf(@PathVariable Long payslipId) {
        byte[] pdf = payslipPdfService.generatePayslipPdf(payslipId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
            "payslip-" + payslipId + ".pdf");
        headers.setContentLength(pdf.length);

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    /**
     * GET /api/payroll/reports/pdf?month=4&year=2026
     * Export semua payslip dalam satu periode — satu file PDF multi-halaman.
     * Setiap karyawan mendapat 1 halaman.
     */
    @GetMapping("/reports/pdf")
    public ResponseEntity<byte[]> exportAllPayslipsPdf(
            @RequestParam @Min(1) @Max(12) int month,
            @RequestParam @Min(2000) int year) {

        byte[] pdf = payslipPdfService.generateAllPayslipsPdf(month, year);

        String periodLabel = Month.of(month)
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toLowerCase()
                + "-" + year;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
            "payroll-payslips-" + periodLabel + ".pdf");
        headers.setContentLength(pdf.length);

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    // ─── Export Excel ─────────────────────────────────────────────────────────

    /**
     * GET /api/payroll/reports/excel?month=4&year=2026
     * Export payroll report seluruh employee dalam satu periode sebagai Excel.
     * Data diambil dari snapshot — tidak ada kalkulasi ulang.
     */
    @GetMapping("/reports/excel")
    public ResponseEntity<byte[]> exportPayrollExcel(
            @RequestParam @Min(1) @Max(12) int month,
            @RequestParam @Min(2000) int year) {

        byte[] excel = payrollReportExcelService.generatePayrollExcel(month, year);

        String periodLabel = Month.of(month)
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toLowerCase()
                + "-" + year;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment",
            "payroll-report-" + periodLabel + ".xlsx");
        headers.setContentLength(excel.length);

        return new ResponseEntity<>(excel, headers, HttpStatus.OK);
    }
}
