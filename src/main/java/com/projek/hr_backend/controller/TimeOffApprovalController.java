package com.projek.hr_backend.controller;

import com.projek.hr_backend.dto.ApiResponse;
import com.projek.hr_backend.dto.TimeOffApprovalRequest;
import com.projek.hr_backend.dto.TimeoffApprovalResponse;
import com.projek.hr_backend.model.User;
import com.projek.hr_backend.service.TimeOffApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time-off-approvals")
@RequiredArgsConstructor
public class TimeOffApprovalController {

    private final TimeOffApprovalService service;

    @GetMapping("/request/{requestId}")
    public ResponseEntity<ApiResponse<List<TimeoffApprovalResponse>>> getByRequest(
            @PathVariable Long requestId) {
        List<TimeoffApprovalResponse> approvals = service.getByRequest(requestId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Approvals retrieved successfully", approvals));
    }

    /**
     * approverId diambil dari JWT — bukan dari request body.
     * Service akan validasi bahwa user yang login adalah approver yang berhak
     * untuk approval record ini.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> approveOrReject(
            @PathVariable Long id,
            @AuthenticationPrincipal User principal,
            @Valid @RequestBody TimeOffApprovalRequest request) {
        Long actorEmployeeId = resolveEmployeeId(principal);
        service.approveOrReject(id, request, actorEmployeeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Approval updated successfully", null));
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private Long resolveEmployeeId(User principal) {
        return principal.getEmployeeId() != null
                ? principal.getEmployeeId()
                : principal.getId();
    }
}
