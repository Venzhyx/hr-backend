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

    @PostMapping("/components")
    public ResponseEntity<ApiResponse<SalaryComponentResponse>> createComponent(
            @Valid @RequestBody SalaryComponentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Salary component created successfully",
                        salaryComponentService.create(request)));
    }

    @GetMapping("/components")
    public ResponseEntity<ApiResponse<List<SalaryComponentResponse>>> getAllComponents(
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        List<SalaryComponentResponse> data = activeOnly
                ? salaryComponentService.getAllActive()
                : salaryComponentService.getAll();
        return ResponseEntity.ok(ApiResponse.success("Salary components retrieved successfully", data));
    }

    @PutMapping("/components/{id}")
    public ResponseEntity<ApiResponse<SalaryComponentResponse>> updateComponent(
            @PathVariable Long id,
            @Valid @RequestBody SalaryComponentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Salary component updated successfully",
                salaryComponentService.update(id, request)));
    }

    @DeleteMapping("/components/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateComponent(@PathVariable Long id) {
        salaryComponentService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success("Salary component deactivated successfully", null));
    }

    // ─── Employee Salary ──────────────────────────────────────────────────────

    @PostMapping("/employee-salary")
    public ResponseEntity<ApiResponse<EmployeeSalaryResponse>> setEmployeeSalary(
            @Valid @RequestBody EmployeeSalaryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee salary set successfully",
                        employeeSalaryService.setBasicSalary(request)));
    }

    @PostMapping("/employee-salary/{id}/components")
    public ResponseEntity<ApiResponse<EmployeeSalaryResponse>> addSalaryComponent(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeSalaryComponentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Salary component added successfully",
                        employeeSalaryService.addComponent(id, request)));
    }

    @GetMapping("/employee-salary/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeSalaryResponse>> getEmployeeSalary(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(ApiResponse.success("Employee salary retrieved successfully",
                employeeSalaryService.getSalaryDetail(employeeId)));
    }

    @GetMapping("/employee-salary/{employeeId}/history")
    public ResponseEntity<ApiResponse<List<EmployeeSalaryResponse>>> getEmployeeSalaryHistory(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(ApiResponse.success("Employee salary history retrieved successfully",
                employeeSalaryService.getSalaryHistory(employeeId)));
    }

    // ─── Payroll Run ──────────────────────────────────────────────────────────

    @PostMapping("/run")
    public ResponseEntity<ApiResponse<PayrollPeriodResponse>> runPayroll(
            @Valid @RequestBody PayrollRunRequest request) {
        PayrollPeriodResponse response = payrollRunService.runPayroll(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Payroll run completed. Generated " + response.getTotalPayslips()
                        + " payslip(s) for " + response.getPeriodLabel(), response));
    }

    @GetMapping("/runs")
    public ResponseEntity<ApiResponse<List<PayrollPeriodResponse>>> getAllPayrollRuns() {
        List<PayrollPeriodResponse> response = payrollRunService.getAllRuns();
        return ResponseEntity.ok(ApiResponse.success("Payroll runs retrieved successfully", response));
    }

    @GetMapping("/runs/{periodId}")
    public ResponseEntity<ApiResponse<PayrollPeriodResponse>> getPayrollRunDetail(
            @PathVariable Long periodId) {
        PayrollPeriodResponse response = payrollRunService.getRunDetail(periodId);
        return ResponseEntity.ok(ApiResponse.success("Payroll run detail retrieved successfully", response));
    }

    /**
     * DELETE /api/payroll/runs?month=4&year=2026
     * Hapus payroll period beserta semua payslip-nya.
     * Hanya boleh jika status period masih DRAFT.
     * Dipakai untuk generate ulang payroll di periode yang sama.
     */
    @DeleteMapping("/runs")
    public ResponseEntity<ApiResponse<Void>> deletePayrollRun(
            @RequestParam @Min(1) @Max(12) int month,
            @RequestParam @Min(2000) int year) {
        payrollRunService.deleteRun(month, year);
        return ResponseEntity.ok(ApiResponse.success("Payroll run deleted successfully", null));
    }

    // ─── Payslip ──────────────────────────────────────────────────────────────

    @GetMapping("/payslips/{employeeId}")
    public ResponseEntity<ApiResponse<List<PayslipResponse>>> getPayslipsByEmployee(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(ApiResponse.success("Payslips retrieved successfully",
                payslipService.getPayslipsByEmployee(employeeId)));
    }

    @GetMapping("/payslips/detail/{payslipId}")
    public ResponseEntity<ApiResponse<PayslipResponse>> getPayslipDetail(
            @PathVariable Long payslipId) {
        return ResponseEntity.ok(ApiResponse.success("Payslip detail retrieved successfully",
                payslipService.getPayslipDetail(payslipId)));
    }

    @PatchMapping("/payslips/{payslipId}/approve")
    public ResponseEntity<ApiResponse<PayslipResponse>> approvePayslip(
            @PathVariable Long payslipId) {
        return ResponseEntity.ok(ApiResponse.success("Payslip approved successfully",
                payslipService.approvePayslip(payslipId)));
    }

    @DeleteMapping("/payslips/{payslipId}")
    public ResponseEntity<ApiResponse<Void>> deletePayslip(
            @PathVariable Long payslipId) {
        payslipService.deletePayslip(payslipId);
        return ResponseEntity.ok(ApiResponse.success("Payslip deleted successfully", null));
    }

    @PatchMapping("/payslips/{payslipId}/paid")
    public ResponseEntity<ApiResponse<PayslipResponse>> markAsPaid(
            @PathVariable Long payslipId) {
        return ResponseEntity.ok(ApiResponse.success("Payslip marked as paid successfully",
                payslipService.markAsPaid(payslipId)));
    }

    // ─── Export PDF ───────────────────────────────────────────────────────────

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
