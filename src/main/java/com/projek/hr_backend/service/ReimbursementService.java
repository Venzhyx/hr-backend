package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.ReimbursementRequest;
import com.projek.hr_backend.dto.ReimbursementResponse;
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
public class ReimbursementService {
    
    private final ReimbursementRepository reimbursementRepository;
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final ApprovalApproverRepository approvalApproverRepository;
    private final ReimbursementApprovalRepository reimbursementApprovalRepository;
    
    @Transactional
    public ReimbursementResponse createReimbursement(ReimbursementRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        
        Reimbursement reimbursement = new Reimbursement();
        reimbursement.setTitle(request.getTitle());
        reimbursement.setExpenseDate(request.getExpenseDate());
        reimbursement.setCategory(request.getCategory());
        reimbursement.setTotal(request.getTotal());
        reimbursement.setEmployee(employee);
        reimbursement.setPaidBy(request.getPaidBy());
        reimbursement.setNotes(request.getNotes());
        reimbursement.setReceiptFile(request.getReceiptFile());
        reimbursement.setStatus(ReimbursementStatus.SUBMITTED);
        
        if (request.getCompanyId() != null) {
            Company company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
            reimbursement.setCompany(company);
        }
        
        Reimbursement saved = reimbursementRepository.save(reimbursement);
        createApprovalRecords(saved);
        return mapToResponse(saved);
    }
    
    private void createApprovalRecords(Reimbursement reimbursement) {
        List<ApprovalApprover> approvers = approvalApproverRepository
                .findAllByOrderByApprovalOrderAsc();
        
        for (ApprovalApprover approver : approvers) {
            ReimbursementApproval approval = new ReimbursementApproval();
            approval.setReimbursement(reimbursement);
            approval.setApprover(approver.getEmployee());
            approval.setStatus(ApprovalStatus.PENDING);
            reimbursementApprovalRepository.save(approval);
        }
    }
    
    public ReimbursementResponse getReimbursement(Long id) {
        Reimbursement reimbursement = reimbursementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reimbursement not found"));
        return mapToResponse(reimbursement);
    }
    
    public List<ReimbursementResponse> getAllReimbursements() {
        return reimbursementRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ReimbursementResponse updateReimbursement(Long id, ReimbursementRequest request) {
        Reimbursement reimbursement = reimbursementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reimbursement not found"));
        
        reimbursement.setTitle(request.getTitle());
        reimbursement.setExpenseDate(request.getExpenseDate());
        reimbursement.setCategory(request.getCategory());
        reimbursement.setTotal(request.getTotal());
        reimbursement.setPaidBy(request.getPaidBy());
        reimbursement.setNotes(request.getNotes());
        reimbursement.setReceiptFile(request.getReceiptFile());
        
        Reimbursement saved = reimbursementRepository.save(reimbursement);
        return mapToResponse(saved);
    }
    
    @Transactional
public void deleteReimbursement(Long id) {
    if (!reimbursementRepository.existsById(id)) {
        throw new ResourceNotFoundException("Reimbursement not found");
    }

    // 🔥 HAPUS SEMUA APPROVAL TERKAIT DULU
    reimbursementApprovalRepository.deleteByReimbursementId(id);

    // 🔥 BARU HAPUS REIMBURSEMENT
    reimbursementRepository.deleteById(id);
}
    
    private ReimbursementResponse mapToResponse(Reimbursement reimbursement) {
        ReimbursementResponse response = new ReimbursementResponse();
        response.setId(reimbursement.getId());
        response.setTitle(reimbursement.getTitle());
        response.setExpenseDate(reimbursement.getExpenseDate());
        response.setCategory(reimbursement.getCategory());
        response.setTotal(reimbursement.getTotal());
        response.setEmployeeId(reimbursement.getEmployee().getId());
        response.setEmployeeName(reimbursement.getEmployee().getName());
        response.setPaidBy(reimbursement.getPaidBy());
        response.setNotes(reimbursement.getNotes());
        response.setReceiptFile(reimbursement.getReceiptFile());
        response.setStatus(reimbursement.getStatus());
        response.setCreatedAt(reimbursement.getCreatedAt());
        response.setUpdatedAt(reimbursement.getUpdatedAt());
        
        if (reimbursement.getCompany() != null) {
            response.setCompanyId(reimbursement.getCompany().getId());
            response.setCompanyName(reimbursement.getCompany().getCompanyName());
        }
        
        return response;
    }
}
