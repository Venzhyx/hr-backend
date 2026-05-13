package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.EmployeeSalaryComponentRequest;
import com.projek.hr_backend.dto.EmployeeSalaryRequest;
import com.projek.hr_backend.dto.EmployeeSalaryResponse;
import com.projek.hr_backend.exception.BadRequestException;
import com.projek.hr_backend.exception.ResourceNotFoundException;
import com.projek.hr_backend.model.*;
import com.projek.hr_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeSalaryService {

    private final EmployeeSalaryRepository employeeSalaryRepository;
    private final EmployeeSalaryComponentRepository employeeSalaryComponentRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryComponentRepository salaryComponentRepository;

    /**
     * Set basic salary untuk employee.
     * Jika sudah ada salary aktif, nonaktifkan dulu sebelum membuat yang baru.
     */
    @Transactional
    public EmployeeSalaryResponse setBasicSalary(EmployeeSalaryRequest request) {
        if (request.getBasicSalary().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Basic salary cannot be negative");
        }

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + request.getEmployeeId()));

        // Nonaktifkan salary aktif yang lama jika ada
        employeeSalaryRepository.findByEmployeeIdAndIsActiveTrue(request.getEmployeeId())
                .ifPresent(existing -> {
                    existing.setIsActive(false);
                    employeeSalaryRepository.save(existing);
                });

        EmployeeSalary salary = new EmployeeSalary();
        salary.setEmployee(employee);
        salary.setBasicSalary(request.getBasicSalary());
        salary.setEffectiveDate(request.getEffectiveDate());
        salary.setIsActive(true);

        EmployeeSalary saved = employeeSalaryRepository.save(salary);
        return buildResponse(saved);
    }

    /**
     * Tambah komponen salary ke employee salary aktif.
     */
    @Transactional
    public EmployeeSalaryResponse addComponent(Long employeeSalaryId, EmployeeSalaryComponentRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Component amount cannot be negative");
        }

        EmployeeSalary employeeSalary = employeeSalaryRepository.findByIdWithEmployee(employeeSalaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee salary not found with id: " + employeeSalaryId));

        SalaryComponent component = salaryComponentRepository.findById(request.getSalaryComponentId())
                .orElseThrow(() -> new ResourceNotFoundException("Salary component not found with id: " + request.getSalaryComponentId()));

        if (!component.getIsActive()) {
            throw new BadRequestException("Salary component '" + component.getName() + "' is not active");
        }

        EmployeeSalaryComponent salaryComponent = new EmployeeSalaryComponent();
        salaryComponent.setEmployeeSalary(employeeSalary);
        salaryComponent.setSalaryComponent(component);
        salaryComponent.setAmount(request.getAmount());

        employeeSalaryComponentRepository.save(salaryComponent);

        // Reload untuk mendapatkan data terbaru
        EmployeeSalary reloaded = employeeSalaryRepository.findByIdWithEmployee(employeeSalaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee salary not found with id: " + employeeSalaryId));
        return buildResponse(reloaded);
    }

    /**
     * Get detail salary aktif employee beserta komponen dan kalkulasi.
     */
    @Transactional(readOnly = true)
    public EmployeeSalaryResponse getSalaryDetail(Long employeeId) {
        EmployeeSalary salary = employeeSalaryRepository.findByEmployeeIdAndIsActiveTrue(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("No active salary found for employee id: " + employeeId));

        return buildResponse(salary);
    }

    /**
     * Get semua riwayat salary employee (aktif dan tidak aktif).
     */
    @Transactional(readOnly = true)
    public List<EmployeeSalaryResponse> getSalaryHistory(Long employeeId) {
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        return employeeSalaryRepository.findByEmployeeId(employeeId).stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private EmployeeSalaryResponse buildResponse(EmployeeSalary salary) {
        List<EmployeeSalaryComponent> components =
                employeeSalaryComponentRepository.findByEmployeeSalaryId(salary.getId());

        List<EmployeeSalaryResponse.ComponentDetail> componentDetails = components.stream()
                .map(c -> new EmployeeSalaryResponse.ComponentDetail(
                        c.getId(),
                        c.getSalaryComponent().getId(),
                        c.getSalaryComponent().getName(),
                        c.getSalaryComponent().getType(),
                        c.getAmount()
                ))
                .collect(Collectors.toList());

        BigDecimal totalEarning = components.stream()
                .filter(c -> c.getSalaryComponent().getType() == SalaryComponentType.EARNING)
                .map(EmployeeSalaryComponent::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDeduction = components.stream()
                .filter(c -> c.getSalaryComponent().getType() == SalaryComponentType.DEDUCTION)
                .map(EmployeeSalaryComponent::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // netSalaryEstimate = basicSalary + totalEarning - totalDeduction
        BigDecimal netSalaryEstimate = salary.getBasicSalary()
                .add(totalEarning)
                .subtract(totalDeduction);

        return new EmployeeSalaryResponse(
                salary.getId(),
                salary.getEmployee().getId(),
                salary.getEmployee().getName(),
                salary.getBasicSalary(),
                salary.getEffectiveDate(),
                salary.getIsActive(),
                componentDetails,
                totalEarning,
                totalDeduction,
                netSalaryEstimate
        );
    }
}
