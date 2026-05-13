package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.PayslipResponse;
import com.projek.hr_backend.exception.BadRequestException;
import com.projek.hr_backend.exception.ResourceNotFoundException;
import com.projek.hr_backend.model.*;
import com.projek.hr_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayslipService {

    private final PayslipRepository          payslipRepository;
    private final PayslipComponentRepository payslipComponentRepository;
    private final EmployeeRepository         employeeRepository;
    private final PayrollPeriodRepository    payrollPeriodRepository;
    private final PayrollRunService          payrollRunService;

    // ─── Query ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PayslipResponse> getPayslipsByEmployee(Long employeeId) {
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Employee not found with id: " + employeeId));

        return payslipRepository.findByEmployeeIdWithPeriod(employeeId).stream()
                .map(p -> {
                    List<PayslipComponent> components =
                            payslipComponentRepository.findByPayslipId(p.getId());
                    return payrollRunService.mapToPayslipResponse(p, components);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PayslipResponse getPayslipDetail(Long payslipId) {
        Payslip payslip = payslipRepository.findByIdWithDetails(payslipId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Payslip not found with id: " + payslipId));

        List<PayslipComponent> components =
                payslipComponentRepository.findByPayslipId(payslipId);

        return payrollRunService.mapToPayslipResponse(payslip, components);
    }

    // ─── Approve per-payslip ──────────────────────────────────────────────────

    /**
     * Approve satu payslip: DRAFT → FINALIZED.
     * Setelah semua payslip dalam periode FINALIZED,
     * status PayrollPeriod otomatis di-sync ke FINALIZED.
     */
    @Transactional
    public PayslipResponse approvePayslip(Long payslipId) {
        Payslip payslip = payslipRepository.findByIdWithDetails(payslipId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Payslip not found with id: " + payslipId));

        if (payslip.getStatus() != PayslipStatus.DRAFT) {
            throw new BadRequestException(
                "Cannot approve payslip. Current status: " + payslip.getStatus().name()
                + ". Only DRAFT payslips can be approved.");
        }

        payslip.setStatus(PayslipStatus.FINALIZED);
        payslipRepository.save(payslip);

        // Sync status period jika semua payslip sudah FINALIZED
        syncPeriodStatus(payslip.getPayrollPeriod());

        List<PayslipComponent> components =
                payslipComponentRepository.findByPayslipId(payslipId);

        return payrollRunService.mapToPayslipResponse(payslip, components);
    }

    // ─── Mark as Paid per-payslip ─────────────────────────────────────────────

    /**
     * Mark satu payslip sebagai PAID: FINALIZED → PAID.
     * Setelah semua payslip dalam periode PAID,
     * status PayrollPeriod otomatis di-sync ke PAID.
     */
    @Transactional
    public PayslipResponse markAsPaid(Long payslipId) {
        Payslip payslip = payslipRepository.findByIdWithDetails(payslipId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Payslip not found with id: " + payslipId));

        if (payslip.getStatus() != PayslipStatus.FINALIZED) {
            throw new BadRequestException(
                "Cannot mark as paid. Current status: " + payslip.getStatus().name()
                + ". Only FINALIZED payslips can be marked as paid.");
        }

        payslip.setStatus(PayslipStatus.PAID);
        payslipRepository.save(payslip);

        // Sync status period jika semua payslip sudah PAID
        syncPeriodStatus(payslip.getPayrollPeriod());

        List<PayslipComponent> components =
                payslipComponentRepository.findByPayslipId(payslipId);

        return payrollRunService.mapToPayslipResponse(payslip, components);
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    /**
     * Hard delete payslip beserta semua komponennya.
     * Hanya boleh jika status payslip masih DRAFT.
     */
    @Transactional
    public void deletePayslip(Long payslipId) {
        Payslip payslip = payslipRepository.findByIdWithDetails(payslipId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Payslip not found with id: " + payslipId));

        if (payslip.getStatus() != PayslipStatus.DRAFT) {
            throw new BadRequestException(
                "Cannot delete payslip. Current status: " + payslip.getStatus().name()
                + ". Only DRAFT payslips can be deleted.");
        }

        payslipComponentRepository.deleteByPayslipId(payslipId);
        payslipRepository.deleteById(payslipId);
    }

    // ─── Sync Period Status ───────────────────────────────────────────────────

    /**
     * Otomatis update status PayrollPeriod berdasarkan status semua payslip-nya.
     * - Semua PAID      → period = PAID
     * - Semua FINALIZED → period = FINALIZED
     * - Ada yang DRAFT  → period tetap DRAFT
     */
    private void syncPeriodStatus(PayrollPeriod period) {
        List<Payslip> allPayslips =
                payslipRepository.findByPayrollPeriodId(period.getId());

        if (allPayslips.isEmpty()) return;

        boolean allPaid = allPayslips.stream()
                .allMatch(p -> p.getStatus() == PayslipStatus.PAID);

        boolean allFinalized = allPayslips.stream()
                .allMatch(p -> p.getStatus() == PayslipStatus.FINALIZED
                            || p.getStatus() == PayslipStatus.PAID);

        if (allPaid) {
            period.setStatus(PayrollPeriodStatus.PAID);
        } else if (allFinalized) {
            period.setStatus(PayrollPeriodStatus.FINALIZED);
        } else {
            period.setStatus(PayrollPeriodStatus.DRAFT);
        }

        payrollPeriodRepository.save(period);
    }
}
