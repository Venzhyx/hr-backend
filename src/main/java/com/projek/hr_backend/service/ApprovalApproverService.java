package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.ApprovalApproverRequest;
import com.projek.hr_backend.dto.ApprovalApproverResponse;
import com.projek.hr_backend.exception.ResourceNotFoundException;
import com.projek.hr_backend.model.ApprovalApprover;
import com.projek.hr_backend.model.Employee;
import com.projek.hr_backend.model.Role;
import com.projek.hr_backend.repository.ApprovalApproverRepository;
import com.projek.hr_backend.repository.EmployeeRepository;
import com.projek.hr_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApprovalApproverService {

    private final ApprovalApproverRepository repository;
    private final EmployeeRepository         employeeRepository;
    private final UserRepository             userRepository;

    /**
     * Ambil semua approver, dengan filter role opsional.
     * Contoh: getAllApprovers("ADMIN") → hanya approver yang user-nya ber-role ADMIN.
     */
    public List<ApprovalApproverResponse> getAllApprovers(String role) {
        List<ApprovalApprover> approvers = repository.findAll();

        if (role != null && !role.isBlank()) {
            Role filterRole;
            try {
                filterRole = Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Role tidak valid: " + role + ". Gunakan ADMIN atau EMPLOYEE.");
            }

            approvers = approvers.stream()
                    .filter(a -> {
                        // Cari user yang punya employeeId ini, cek role-nya
                        return userRepository.findByEmployeeId(a.getEmployee().getId())
                                .map(u -> u.getRole() == filterRole)
                                .orElse(false);
                    })
                    .collect(Collectors.toList());
        }

        return approvers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ApprovalApproverResponse createApprover(ApprovalApproverRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        // Validasi: hanya user ber-role ADMIN yang bisa dijadikan approver
        userRepository.findByEmployeeId(request.getEmployeeId())
                .ifPresent(user -> {
                    if (user.getRole() != Role.ADMIN) {
                        throw new IllegalArgumentException(
                            "Hanya user dengan role ADMIN yang bisa dijadikan approver. " +
                            "Employee ini ber-role: " + user.getRole().name());
                    }
                });

        ApprovalApprover approver = new ApprovalApprover();
        approver.setEmployee(employee);
        approver.setIsRequired(request.getIsRequired());
        approver.setApprovalOrder(request.getApprovalOrder());
        approver.setMinimumApproval(request.getMinimumApproval());

        ApprovalApprover saved = repository.save(approver);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteApprover(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Approver not found");
        }
        repository.deleteById(id);
    }

    private ApprovalApproverResponse mapToResponse(ApprovalApprover approver) {
        return new ApprovalApproverResponse(
            approver.getId(),
            approver.getEmployee().getId(),
            approver.getEmployee().getName(),
            approver.getIsRequired(),
            approver.getApprovalOrder(),
            approver.getMinimumApproval(),
            approver.getCreatedAt()
        );
    }
}
