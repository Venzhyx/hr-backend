package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.ApprovalApproverRequest;
import com.projek.hr_backend.dto.ApprovalApproverResponse;
import com.projek.hr_backend.exception.ResourceNotFoundException;
import com.projek.hr_backend.model.ApprovalApprover;
import com.projek.hr_backend.model.Employee;
import com.projek.hr_backend.repository.ApprovalApproverRepository;
import com.projek.hr_backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApprovalApproverService {
    
    private final ApprovalApproverRepository repository;
    private final EmployeeRepository employeeRepository;
    
    public List<ApprovalApproverResponse> getAllApprovers() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ApprovalApproverResponse createApprover(ApprovalApproverRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        
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
