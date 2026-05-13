    package com.projek.hr_backend.controller;

    import com.projek.hr_backend.dto.ApiResponse;
    import com.projek.hr_backend.dto.ApprovalApproverRequest;
    import com.projek.hr_backend.dto.ApprovalApproverResponse;
    import com.projek.hr_backend.service.ApprovalApproverService;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/approval-approvers")
    @RequiredArgsConstructor
    @CrossOrigin(originPatterns = "*")
    public class ApprovalApproverController {
        
        private final ApprovalApproverService service;
        
        @GetMapping
        public ResponseEntity<ApiResponse<List<ApprovalApproverResponse>>> getAllApprovers() {
            List<ApprovalApproverResponse> responses = service.getAllApprovers();
            return ResponseEntity.ok(new ApiResponse<>(true, "Approvers retrieved successfully", responses));
        }
        
        @PostMapping
        public ResponseEntity<ApiResponse<ApprovalApproverResponse>> createApprover(
                @Valid @RequestBody ApprovalApproverRequest request) {
            ApprovalApproverResponse response = service.createApprover(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Approver created successfully", response));
        }
        
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> deleteApprover(@PathVariable Long id) {
            service.deleteApprover(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Approver deleted successfully", null));
        }
    }
