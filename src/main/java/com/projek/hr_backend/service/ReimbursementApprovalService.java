package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.ReimbursementApprovalRequest;
import com.projek.hr_backend.dto.ReimbursementApprovalResponse;
import com.projek.hr_backend.exception.ResourceNotFoundException;
import com.projek.hr_backend.model.*;
import com.projek.hr_backend.repository.ApprovalApproverRepository;
import com.projek.hr_backend.repository.ReimbursementApprovalRepository;
import com.projek.hr_backend.repository.ReimbursementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReimbursementApprovalService {

    private final ReimbursementApprovalRepository approvalRepository;
    private final ReimbursementRepository reimbursementRepository;
    private final ApprovalApproverRepository approverRepository;

    @Transactional(readOnly = true)
    public List<ReimbursementApprovalResponse> getAllApprovals() {
        return approvalRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReimbursementApprovalResponse getApprovalById(Long id) {
        ReimbursementApproval approval = approvalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Approval not found with id: " + id));
        return mapToResponse(approval);
    }

    @Transactional(readOnly = true)
    public List<ReimbursementApprovalResponse> getApprovalsByReimbursementId(Long reimbursementId) {
        List<ReimbursementApproval> approvals = approvalRepository.findByReimbursementId(reimbursementId);
        return approvals.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveOrRejectApproval(Long approvalId, ReimbursementApprovalRequest request) {
        String action = request.getAction().toUpperCase();
        if (!action.equals("APPROVED") && !action.equals("REJECTED")) {
            throw new IllegalArgumentException("Invalid action. Must be APPROVED or REJECTED");
        }

        ReimbursementApproval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new ResourceNotFoundException("Approval not found with id: " + approvalId));

        ApprovalStatus status = ApprovalStatus.valueOf(action);
        approval.setStatus(status);
        approval.setNotes(request.getNotes());
        approval.setApprovedAt(LocalDateTime.now());

        approvalRepository.save(approval);

        updateReimbursementStatus(approval.getReimbursement().getId());
    }

    private void updateReimbursementStatus(Long reimbursementId) {
        Reimbursement reimbursement = reimbursementRepository.findById(reimbursementId)
                .orElseThrow(() -> new ResourceNotFoundException("Reimbursement not found"));

        List<ReimbursementApproval> allApprovals = approvalRepository.findByReimbursementId(reimbursementId);

        // Jika ada yang REJECTED → langsung REJECTED
        boolean hasRejected = allApprovals.stream()
                .anyMatch(a -> a.getStatus() == ApprovalStatus.REJECTED);
        if (hasRejected) {
            reimbursement.setStatus(ReimbursementStatus.REJECTED);
            reimbursementRepository.save(reimbursement);
            return;
        }

        long approvedCount = allApprovals.stream()
                .filter(a -> a.getStatus() == ApprovalStatus.APPROVED)
                .count();

        List<ApprovalApprover> approvers = approverRepository.findAllByOrderByApprovalOrderAsc();

        // Kalau tidak ada approver sama sekali → langsung APPROVED
        if (approvers.isEmpty()) {
            reimbursement.setStatus(ReimbursementStatus.APPROVED);
            reimbursementRepository.save(reimbursement);
            return;
        }

        long minimumApproval = approvers.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsRequired()))
                .count();

        // Fallback: kalau semua optional, minimum = total approver
        if (minimumApproval == 0) {
            minimumApproval = approvers.size();
        }

        if (approvedCount == 0) {
    reimbursement.setStatus(ReimbursementStatus.SUBMITTED);
} else if (approvedCount < minimumApproval) {
    reimbursement.setStatus(ReimbursementStatus.PENDING);
} else {
    reimbursement.setStatus(ReimbursementStatus.APPROVED);
}

        reimbursementRepository.save(reimbursement);
    }

    private ReimbursementApprovalResponse mapToResponse(ReimbursementApproval approval) {
        ReimbursementApprovalResponse response = new ReimbursementApprovalResponse();
        response.setId(approval.getId());
        response.setReimbursementId(approval.getReimbursement().getId());
        response.setReimbursementTitle(approval.getReimbursement().getTitle());
        response.setApproverId(approval.getApprover().getId());
        response.setApproverName(approval.getApprover().getName());
        response.setStatus(approval.getStatus());
        response.setNotes(approval.getNotes());
        response.setApprovedAt(approval.getApprovedAt());
        response.setCreatedAt(approval.getCreatedAt());
        return response;
    }
}
