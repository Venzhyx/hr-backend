package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.PayrollPeriodResponse;
import com.projek.hr_backend.dto.PayrollRunRequest;
import com.projek.hr_backend.dto.PayslipResponse;
import com.projek.hr_backend.exception.DuplicateResourceException;
import com.projek.hr_backend.exception.ResourceNotFoundException;
import com.projek.hr_backend.model.*;
import com.projek.hr_backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollRunService {

    // Rate konstanta — bisa dipindah ke tabel settings nanti
    private static final BigDecimal OVERTIME_RATE_PER_HOUR   = new BigDecimal("25000");
    private static final BigDecimal ABSENT_DEDUCTION_PER_DAY = new BigDecimal("100000");
    private static final BigDecimal LATE_DEDUCTION_PER_DAY   = new BigDecimal("25000");

    private final PayrollPeriodRepository           payrollPeriodRepository;
    private final PayslipRepository                 payslipRepository;
    private final PayslipComponentRepository        payslipComponentRepository;
    private final EmployeeRepository                employeeRepository;
    private final EmployeeSalaryRepository          employeeSalaryRepository;
    private final EmployeeSalaryComponentRepository employeeSalaryComponentRepository;
    private final OvertimeRepository                overtimeRepository;
    private final AttendanceRepository              attendanceRepository;

    // ─── Query Runs ───────────────────────────────────────────────────────────

    /**
     * Ambil semua payroll period, diurutkan terbaru dulu.
     * Setiap period sudah include semua payslip-nya.
     */
    @Transactional(readOnly = true)
    public List<PayrollPeriodResponse> getAllRuns() {
        return payrollPeriodRepository
                .findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(period -> {
                    List<Payslip> payslips = payslipRepository.findByPayrollPeriodId(period.getId());
                    return buildPeriodResponse(period, payslips,
                            payslips.size(), payslips.size(), 0, 0);
                })
                .collect(Collectors.toList());
    }

    /**
     * Ambil detail satu payroll period beserta semua payslip-nya.
     */
    @Transactional(readOnly = true)
    public PayrollPeriodResponse getRunDetail(Long periodId) {
        PayrollPeriod period = payrollPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payroll period not found with id: " + periodId));

        List<Payslip> payslips = payslipRepository.findByPayrollPeriodId(period.getId());
        return buildPeriodResponse(period, payslips,
                payslips.size(), payslips.size(), 0, 0);
    }

    // ─── Run Payroll ──────────────────────────────────────────────────────────

    @Transactional
    public PayrollPeriodResponse runPayroll(PayrollRunRequest request) {
        int month = request.getMonth();
        int year  = request.getYear();

        // Immutability: periode yang sama tidak boleh di-run dua kali
        if (payrollPeriodRepository.existsByMonthAndYear(month, year)) {
            throw new DuplicateResourceException(
                "Payroll for " + buildPeriodLabel(month, year) + " has already been run. "
                + "Payroll is immutable — create a new period or contact administrator.");
        }

        // 1. Buat PayrollPeriod
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate   = startDate.withDayOfMonth(startDate.lengthOfMonth());

        PayrollPeriod period = new PayrollPeriod();
        period.setMonth(month);
        period.setYear(year);
        period.setStartDate(startDate);
        period.setEndDate(endDate);
        period.setStatus(PayrollPeriodStatus.DRAFT);
        period = payrollPeriodRepository.save(period);

        log.info("Payroll run started for period: {}", buildPeriodLabel(month, year));

        // 2. Ambil semua employee dan proses satu per satu
        List<Employee> employees = employeeRepository.findAll();
        List<Payslip>  generatedPayslips = new ArrayList<>();

        int successCount  = 0;
        int skippedCount  = 0;
        int failedCount   = 0;

        for (Employee employee : employees) {
            try {
                Payslip payslip = processEmployeePayroll(employee, period, startDate, endDate);
                generatedPayslips.add(payslip);
                successCount++;
            } catch (ResourceNotFoundException ex) {
                // Tidak punya salary aktif — skip, bukan error
                skippedCount++;
                log.warn("SKIPPED employee id={} name='{}': {}",
                    employee.getId(), employee.getName(), ex.getMessage());
            } catch (Exception ex) {
                // Error tak terduga — catat tapi jangan hentikan proses
                failedCount++;
                log.error("FAILED employee id={} name='{}': {}",
                    employee.getId(), employee.getName(), ex.getMessage(), ex);
            }
        }

        log.info("Payroll run completed for {}. success={} skipped={} failed={}",
            buildPeriodLabel(month, year), successCount, skippedCount, failedCount);

        return buildPeriodResponse(period, generatedPayslips,
            employees.size(), successCount, skippedCount, failedCount);
    }

    // ─── Proses per Employee ──────────────────────────────────────────────────

    private Payslip processEmployeePayroll(Employee employee, PayrollPeriod period,
                                           LocalDate startDate, LocalDate endDate) {

        // A. Ambil EmployeeSalary aktif — wajib ada, kalau tidak ada throw untuk di-skip
        EmployeeSalary employeeSalary = employeeSalaryRepository
                .findByEmployeeIdAndIsActiveTrue(employee.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "No active salary found for employee: " + employee.getName()));

        BigDecimal basicSalary = employeeSalary.getBasicSalary();

        // Ambil komponen salary employee
        List<EmployeeSalaryComponent> salaryComponents =
                employeeSalaryComponentRepository.findByEmployeeSalaryId(employeeSalary.getId());

        // B. Hitung EARNING dari komponen salary setup
        BigDecimal totalEarning = salaryComponents.stream()
                .filter(c -> c.getSalaryComponent().getType() == SalaryComponentType.EARNING)
                .map(EmployeeSalaryComponent::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // C. Hitung DEDUCTION dari komponen salary setup
        BigDecimal totalDeduction = salaryComponents.stream()
                .filter(c -> c.getSalaryComponent().getType() == SalaryComponentType.DEDUCTION)
                .map(EmployeeSalaryComponent::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // D. Hitung OVERTIME — hanya status APPROVED dalam periode ini
        Double rawOvertimeHours = overtimeRepository
                .getTotalApprovedHoursByEmployeeAndMonth(
                    employee.getId(), period.getMonth(), period.getYear());
        double totalOvertimeHours = rawOvertimeHours != null ? rawOvertimeHours : 0.0;
        BigDecimal overtimePay = OVERTIME_RATE_PER_HOUR
                .multiply(BigDecimal.valueOf(totalOvertimeHours));

        // E. Hitung potongan ABSENT dan LATE dari attendance dalam periode
        long absentCount = attendanceRepository.countByEmployeeIdAndStatusAndDateBetween(
                employee.getId(), "ABSENT", startDate, endDate);
        long lateCount   = attendanceRepository.countByEmployeeIdAndStatusAndDateBetween(
                employee.getId(), "LATE", startDate, endDate);

        BigDecimal absentDeduction = ABSENT_DEDUCTION_PER_DAY.multiply(BigDecimal.valueOf(absentCount));
        BigDecimal lateDeduction   = LATE_DEDUCTION_PER_DAY.multiply(BigDecimal.valueOf(lateCount));
        totalDeduction = totalDeduction.add(absentDeduction).add(lateDeduction);

        // F. Hitung NET SALARY
        // netSalary = basicSalary + totalEarning + overtimePay - totalDeduction
        BigDecimal netSalary = basicSalary
                .add(totalEarning)
                .add(overtimePay)
                .subtract(totalDeduction);

        // G. Simpan Payslip — immutable, semua field updatable=false di entity
        Payslip payslip = new Payslip();
        payslip.setPayrollPeriod(period);
        payslip.setEmployee(employee);
        payslip.setBasicSalary(basicSalary);
        payslip.setTotalEarning(totalEarning);
        payslip.setTotalDeduction(totalDeduction);
        payslip.setOvertimePay(overtimePay);
        payslip.setTotalOvertimeHours(totalOvertimeHours);
        payslip.setNetSalary(netSalary);
        payslip.setTotalAbsent((int) absentCount);
        payslip.setTotalLate((int) lateCount);
        payslip.setStatus(PayslipStatus.DRAFT);
        payslip = payslipRepository.save(payslip);

        // H. Simpan PayslipComponent — snapshot permanen semua rincian
        List<PayslipComponent> components = new ArrayList<>();

        // Earning dari salary setup
        for (EmployeeSalaryComponent sc : salaryComponents) {
            if (sc.getSalaryComponent().getType() == SalaryComponentType.EARNING) {
                components.add(buildComponent(payslip,
                    sc.getSalaryComponent().getName(),
                    PayslipComponentType.EARNING,
                    sc.getAmount()));
            }
        }

        // Deduction dari salary setup
        for (EmployeeSalaryComponent sc : salaryComponents) {
            if (sc.getSalaryComponent().getType() == SalaryComponentType.DEDUCTION) {
                components.add(buildComponent(payslip,
                    sc.getSalaryComponent().getName(),
                    PayslipComponentType.DEDUCTION,
                    sc.getAmount()));
            }
        }

        // Overtime pay sebagai komponen earning (hanya jika ada)
        if (overtimePay.compareTo(BigDecimal.ZERO) > 0) {
            components.add(buildComponent(payslip,
                "Overtime Pay (" + String.format("%.1f", totalOvertimeHours) + " hrs)",
                PayslipComponentType.EARNING,
                overtimePay));
        }

        // Potongan absen (hanya jika ada)
        if (absentDeduction.compareTo(BigDecimal.ZERO) > 0) {
            components.add(buildComponent(payslip,
                "Absent Deduction (" + absentCount + " day(s))",
                PayslipComponentType.DEDUCTION,
                absentDeduction));
        }

        // Potongan telat (hanya jika ada)
        if (lateDeduction.compareTo(BigDecimal.ZERO) > 0) {
            components.add(buildComponent(payslip,
                "Late Deduction (" + lateCount + " day(s))",
                PayslipComponentType.DEDUCTION,
                lateDeduction));
        }

        payslipComponentRepository.saveAll(components);

        return payslip;
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private PayslipComponent buildComponent(Payslip payslip, String name,
                                            PayslipComponentType type, BigDecimal amount) {
        PayslipComponent c = new PayslipComponent();
        c.setPayslip(payslip);
        c.setComponentName(name);
        c.setType(type);
        c.setAmount(amount);
        return c;
    }

    private PayrollPeriodResponse buildPeriodResponse(PayrollPeriod period,
                                                       List<Payslip> payslips,
                                                       int totalEmployees,
                                                       int successCount,
                                                       int skippedCount,
                                                       int failedCount) {
        List<PayslipResponse> payslipResponses = payslips.stream()
                .map(p -> {
                    List<PayslipComponent> components =
                            payslipComponentRepository.findByPayslipId(p.getId());
                    return mapToPayslipResponse(p, components);
                })
                .collect(Collectors.toList());

        PayrollPeriodResponse response = new PayrollPeriodResponse();
        response.setId(period.getId());
        response.setMonth(period.getMonth());
        response.setYear(period.getYear());
        response.setPeriodLabel(buildPeriodLabel(period.getMonth(), period.getYear()));
        response.setStartDate(period.getStartDate());
        response.setEndDate(period.getEndDate());
        response.setStatus(period.getStatus());
        response.setTotalEmployees(totalEmployees);
        response.setSuccessCount(successCount);
        response.setSkippedCount(skippedCount);
        response.setFailedCount(failedCount);
        response.setTotalPayslips(successCount);   // backward compat
        response.setPayslips(payslipResponses);
        response.setCreatedAt(period.getCreatedAt());
        response.setUpdatedAt(period.getUpdatedAt());
        return response;
    }

    public PayslipResponse mapToPayslipResponse(Payslip p, List<PayslipComponent> components) {
        List<PayslipResponse.ComponentItem> items = components.stream()
                .map(c -> new PayslipResponse.ComponentItem(
                        c.getId(), c.getComponentName(), c.getType(), c.getAmount()))
                .collect(Collectors.toList());

        PayslipResponse response = new PayslipResponse();
        response.setId(p.getId());
        response.setEmployeeId(p.getEmployee().getId());
        response.setEmployeeName(p.getEmployee().getName());
        response.setPeriodId(p.getPayrollPeriod().getId());
        response.setPeriodLabel(buildPeriodLabel(
            p.getPayrollPeriod().getMonth(), p.getPayrollPeriod().getYear()));
        response.setMonth(p.getPayrollPeriod().getMonth());
        response.setYear(p.getPayrollPeriod().getYear());
        // Status per-payslip — diambil dari field status payslip itu sendiri
        response.setStatus(p.getStatus());
        response.setBasicSalary(p.getBasicSalary());
        response.setOvertimePay(p.getOvertimePay());
        response.setTotalOvertimeHours(p.getTotalOvertimeHours());
        response.setTotalEarning(p.getTotalEarning());
        response.setTotalDeduction(p.getTotalDeduction());
        response.setNetSalary(p.getNetSalary());
        response.setTotalAbsent(p.getTotalAbsent());
        response.setTotalLate(p.getTotalLate());
        response.setGeneratedAt(p.getGeneratedAt());
        response.setComponents(items);
        return response;
    }

    private String buildPeriodLabel(int month, int year) {
        return Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + year;
    }
}
