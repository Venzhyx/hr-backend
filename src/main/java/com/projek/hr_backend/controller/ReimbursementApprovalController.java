package com.projek.hr_backend.controller;

import com.projek.hr_backend.dto.ApiResponse;
import com.projek.hr_backend.dto.ReimbursementApprovalRequest;
import com.projek.hr_backend.dto.ReimbursementApprovalResponse;
import com.projek.hr_backend.model.User;
import com.projek.hr_backend.service.ReimbursementApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reimbursement-approvals")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class ReimbursementApprovalController {

    private final ReimbursementApprovalService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReimbursementApprovalResponse>>> getAllApprovals() {
        List<ReimbursementApprovalResponse> approvals = service.getAllApprovals();
        return ResponseEntity.ok(new ApiResponse<>(true, "Approvals retrieved successfully", approvals));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReimbursementApprovalResponse>> getApprovalById(
            @PathVariable Long id) {
        ReimbursementApprovalResponse approval = service.getApprovalById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Approval retrieved successfully", approval));
    }

    @GetMapping("/reimbursement/{reimbursementId}")
    public ResponseEntity<ApiResponse<List<ReimbursementApprovalResponse>>> getApprovalsByReimbursementId(
            @PathVariable Long reimbursementId) {
        List<ReimbursementApprovalResponse> approvals =
                service.getApprovalsByReimbursementId(reimbursementId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Approvals retrieved successfully", approvals));
    }

    /**
     * approverId diambil dari JWT — bukan dari request body.
     * Service akan validasi bahwa user yang login adalah approver yang berhak
     * untuk approval record ini.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> approveOrRejectApproval(
            @PathVariable Long id,
            @AuthenticationPrincipal User principal,
            @Valid @RequestBody ReimbursementApprovalRequest request) {
        Long actorEmployeeId = resolveEmployeeId(principal);
        service.approveOrRejectApproval(id, request, actorEmployeeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Approval updated successfully", null));
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private Long resolveEmployeeId(User principal) {
        return principal.getEmployeeId() != null
                ? principal.getEmployeeId()
                : principal.getId();
    }
}
