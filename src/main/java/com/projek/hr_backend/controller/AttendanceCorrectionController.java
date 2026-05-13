package com.projek.hr_backend.controller;

import com.projek.hr_backend.dto.ApiResponse;
import com.projek.hr_backend.dto.AttendanceCorrectionRequest;
import com.projek.hr_backend.dto.AttendanceCorrectionResponse;
import com.projek.hr_backend.service.AttendanceCorrectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance-corrections")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AttendanceCorrectionController {

    private final AttendanceCorrectionService service;

    @PostMapping
    public ResponseEntity<ApiResponse<AttendanceCorrectionResponse>> createCorrection(
            @Valid @RequestBody AttendanceCorrectionRequest request) {
        AttendanceCorrectionResponse response = service.createCorrection(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Correction request created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AttendanceCorrectionResponse>>> getAllCorrections() {
        List<AttendanceCorrectionResponse> responses = service.getAllCorrections();
        return ResponseEntity.ok(new ApiResponse<>(true, "Corrections retrieved successfully", responses));
    }

    @GetMapping("/my/{employeeId}")
    public ResponseEntity<ApiResponse<List<AttendanceCorrectionResponse>>> getMyCorrections(
            @PathVariable Long employeeId) {
        List<AttendanceCorrectionResponse> responses = service.getCorrectionsByEmployee(employeeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Corrections retrieved successfully", responses));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<AttendanceCorrectionResponse> approveCorrection(
            @PathVariable Long id,
            @RequestParam Long approverId,
            @RequestBody(required = false) Map<String, String> body) {
        
        String notes = body != null ? body.get("notes") : null;
        AttendanceCorrectionResponse response = service.approveCorrection(id, approverId, notes);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<AttendanceCorrectionResponse> rejectCorrection(
            @PathVariable Long id,
            @RequestParam Long approverId,
            @RequestBody Map<String, String> body) {
        
        String notes = body.get("notes");
        if (notes == null || notes.isEmpty()) {
            throw new IllegalArgumentException("Notes are required for rejection");
        }
        AttendanceCorrectionResponse response = service.rejectCorrection(id, approverId, notes);
        return ResponseEntity.ok(response);
    }

    // ✅ TAMBAH: Update Correction (hanya untuk status SUBMITTED)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendanceCorrectionResponse>> updateCorrection(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceCorrectionRequest request) {
        AttendanceCorrectionResponse response = service.updateCorrection(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Correction updated successfully", response));
    }

    // ✅ TAMBAH: Delete Correction (hanya untuk status SUBMITTED)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCorrection(@PathVariable Long id) {
        service.deleteCorrection(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Correction deleted successfully", null));
    }
}