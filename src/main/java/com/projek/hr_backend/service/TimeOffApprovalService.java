package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.TimeOffApprovalRequest;
import com.projek.hr_backend.dto.TimeoffApprovalResponse;
import com.projek.hr_backend.exception.ResourceNotFoundException;
import com.projek.hr_backend.model.ApprovalStatus;
import com.projek.hr_backend.model.TimeOffApproval;
import com.projek.hr_backend.model.TimeOffRequest;
import com.projek.hr_backend.model.TimeOffRequestStatus;
import com.projek.hr_backend.repository.TimeOffApprovalRepository;
import com.projek.hr_backend.repository.TimeOffRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeOffApprovalService {

    private final TimeOffApprovalRepository timeOffApprovalRepository;
    private final TimeOffRequestRepository timeOffRequestRepository;

    // ── GET semua approval records untuk 1 request ──────────────────────────
    public List<TimeoffApprovalResponse> getByRequest(Long requestId) {
        return timeOffApprovalRepository.findByTimeOffRequestId(requestId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ── PATCH: approve atau reject ───────────────────────────────────────────
    @Transactional
    public void approveOrReject(Long approvalId, TimeOffApprovalRequest request) {
        TimeOffApproval approval = timeOffApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new ResourceNotFoundException("Approval record not found"));

        if (approval.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalStateException("Approval sudah diproses sebelumnya.");
        }

        String action = request.getAction().toUpperCase();

        if (action.equals("APPROVED")) {
            approval.setStatus(ApprovalStatus.APPROVED);
        } else if (action.equals("REJECTED")) {
            approval.setStatus(ApprovalStatus.REJECTED);
        } else {
            throw new IllegalArgumentException("Action tidak valid. Gunakan APPROVED atau REJECTED.");
        }

        approval.setNotes(request.getNotes());
        approval.setActionAt(LocalDateTime.now());
        timeOffApprovalRepository.save(approval);

        // Update status TimeOffRequest berdasarkan hasil approval
        updateTimeOffRequestStatus(approval.getTimeOffRequest().getId());
    }

    // ── Update status induk request setelah approval diproses ───────────────
    private void updateTimeOffRequestStatus(Long requestId) {
        List<TimeOffApproval> all = timeOffApprovalRepository.findByTimeOffRequestId(requestId);

        long totalApprovers  = all.size();
        long approvedCount   = all.stream().filter(a -> a.getStatus() == ApprovalStatus.APPROVED).count();
        long rejectedCount   = all.stream().filter(a -> a.getStatus() == ApprovalStatus.REJECTED).count();

        TimeOffRequest timeOffRequest = timeOffRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Time off request not found"));

        if (rejectedCount > 0) {
            // Ada yang reject → langsung REJECTED
            timeOffRequest.setStatus(TimeOffRequestStatus.REJECTED);
        } else if (approvedCount == totalApprovers) {
            // Semua sudah approve → APPROVED
            timeOffRequest.setStatus(TimeOffRequestStatus.APPROVED);
        } else if (approvedCount > 0) {
            // Ada yang sudah approve tapi belum semua → PENDING
            timeOffRequest.setStatus(TimeOffRequestStatus.PENDING);
        }
        // Kalau approvedCount == 0 && rejectedCount == 0 → tetap SUBMITTED, tidak diubah

        timeOffRequestRepository.save(timeOffRequest);
    }

    // ── Mapper entity → DTO ──────────────────────────────────────────────────
    private TimeoffApprovalResponse mapToResponse(TimeOffApproval approval) {
        TimeoffApprovalResponse dto = new TimeoffApprovalResponse();
        dto.setId(approval.getId());
        dto.setTimeOffRequestId(approval.getTimeOffRequest().getId());
        dto.setApproverId(approval.getApprover().getId());
        dto.setApproverName(approval.getApprover().getName());
        dto.setStatus(approval.getStatus());
        dto.setNotes(approval.getNotes());
        dto.setActionAt(approval.getActionAt());
        dto.setCreatedAt(approval.getCreatedAt());
        return dto;
    }
}
