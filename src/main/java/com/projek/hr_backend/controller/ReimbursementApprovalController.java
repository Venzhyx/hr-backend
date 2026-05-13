package com.projek.hr_backend.controller;

import com.projek.hr_backend.dto.ApiResponse;
import com.projek.hr_backend.dto.ReimbursementApprovalRequest;
import com.projek.hr_backend.dto.ReimbursementApprovalResponse;
import com.projek.hr_backend.service.ReimbursementApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<ReimbursementApprovalResponse>> getApprovalById(@PathVariable Long id) {
        ReimbursementApprovalResponse approval = service.getApprovalById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Approval retrieved successfully", approval));
    }

    @GetMapping("/reimbursement/{reimbursementId}")
    public ResponseEntity<ApiResponse<List<ReimbursementApprovalResponse>>> getApprovalsByReimbursementId(
            @PathVariable Long reimbursementId) {
        List<ReimbursementApprovalResponse> approvals = service.getApprovalsByReimbursementId(reimbursementId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Approvals retrieved successfully", approvals));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> approveOrRejectApproval(
            @PathVariable Long id,
            @Valid @RequestBody ReimbursementApprovalRequest request) {
        service.approveOrRejectApproval(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Approval updated successfully", null));
    }
}
