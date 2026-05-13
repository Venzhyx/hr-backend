package com.projek.hr_backend.controller;

import com.projek.hr_backend.dto.ApiResponse;
import com.projek.hr_backend.dto.OvertimeRequest;
import com.projek.hr_backend.dto.OvertimeResponse;
import com.projek.hr_backend.service.OvertimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/overtimes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OvertimeController {

    private final OvertimeService service;

    @PostMapping
    public ResponseEntity<ApiResponse<OvertimeResponse>> createOvertime(
            @Valid @RequestBody OvertimeRequest request) {
        OvertimeResponse response = service.createOvertime(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Overtime request created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OvertimeResponse>>> getAllOvertimes() {
        List<OvertimeResponse> responses = service.getAllOvertimes();
        return ResponseEntity.ok(new ApiResponse<>(true, "Overtimes retrieved successfully", responses));
    }

    @GetMapping("/my/{employeeId}")
    public ResponseEntity<ApiResponse<List<OvertimeResponse>>> getMyOvertimes(
            @PathVariable Long employeeId) {
        List<OvertimeResponse> responses = service.getOvertimesByEmployee(employeeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Overtimes retrieved successfully", responses));
    }

    @GetMapping("/total/{employeeId}")
    public ResponseEntity<ApiResponse<Double>> getTotalOvertime(
            @PathVariable Long employeeId,
            @RequestParam int month,
            @RequestParam int year) {
        Double total = service.getTotalOvertimeByEmployee(employeeId, month, year);
        return ResponseEntity.ok(new ApiResponse<>(true, "Total overtime hours retrieved", total));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<OvertimeResponse>> approveOvertime(
            @PathVariable Long id,
            @RequestParam Long approverId,
            @RequestBody(required = false) Map<String, String> body) {

        String notes = body != null ? body.get("notes") : null;
        OvertimeResponse response = service.approveOvertime(id, approverId, notes);
        return ResponseEntity.ok(new ApiResponse<>(true, "Overtime approved", response));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<OvertimeResponse>> rejectOvertime(
            @PathVariable Long id,
            @RequestParam Long approverId,
            @RequestBody Map<String, String> body) {

        String notes = body.get("notes");
        OvertimeResponse response = service.rejectOvertime(id, approverId, notes);
        return ResponseEntity.ok(new ApiResponse<>(true, "Overtime rejected", response));
    }

    // ✅ TAMBAHKAN INI - UPDATE Overtime
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OvertimeResponse>> updateOvertime(
            @PathVariable Long id,
            @Valid @RequestBody OvertimeRequest request) {
        OvertimeResponse response = service.updateOvertime(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Overtime updated successfully", response));
    }

    // ✅ TAMBAHKAN INI - DELETE Overtime
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOvertime(@PathVariable Long id) {
        service.deleteOvertime(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Overtime deleted successfully", null));
    }
}