package com.projek.hr_backend.controller;

import com.projek.hr_backend.dto.ApiResponse;
import com.projek.hr_backend.dto.TimeOffRequestRequest;
import com.projek.hr_backend.dto.TimeOffRequestResponse;
import com.projek.hr_backend.service.TimeOffRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time-off-requests")
@RequiredArgsConstructor
// @CrossOrigin dihapus — sudah dihandle global oleh CorsConfig.java
public class TimeOffRequestController {

    private final TimeOffRequestService service;

    @PostMapping
    public ResponseEntity<ApiResponse<TimeOffRequestResponse>> createRequest(
            @Valid @RequestBody TimeOffRequestRequest request) {
        TimeOffRequestResponse response = service.createRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Time off request created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TimeOffRequestResponse>>> getAllRequests() {
        List<TimeOffRequestResponse> responses = service.getAllRequests();
        return ResponseEntity.ok(new ApiResponse<>(true, "Time off requests retrieved successfully", responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TimeOffRequestResponse>> getRequestById(@PathVariable Long id) {
        TimeOffRequestResponse response = service.getRequestById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Time off request retrieved successfully", response));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<TimeOffRequestResponse>>> getRequestsByEmployeeId(
            @PathVariable Long employeeId) {
        List<TimeOffRequestResponse> responses = service.getRequestsByEmployeeId(employeeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Time off requests retrieved successfully", responses));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TimeOffRequestResponse>> updateRequest(
            @PathVariable Long id,
            @Valid @RequestBody TimeOffRequestRequest request) {
        TimeOffRequestResponse response = service.updateRequest(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Time off request updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRequest(@PathVariable Long id) {
        service.deleteRequest(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Time off request deleted successfully", null));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<TimeOffRequestResponse>> approveRequest(@PathVariable Long id) {
        TimeOffRequestResponse response = service.approveRequest(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Time off request approved successfully", response));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<TimeOffRequestResponse>> rejectRequest(@PathVariable Long id) {
        TimeOffRequestResponse response = service.rejectRequest(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Time off request rejected successfully", response));
    }
}
