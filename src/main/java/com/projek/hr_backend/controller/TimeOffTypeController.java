package com.projek.hr_backend.controller;

import com.projek.hr_backend.dto.ApiResponse;
import com.projek.hr_backend.dto.TimeOffTypeRequest;
import com.projek.hr_backend.dto.TimeOffTypeResponse;
import com.projek.hr_backend.service.TimeOffTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time-off-types")
@RequiredArgsConstructor
public class TimeOffTypeController {
    
    private final TimeOffTypeService service;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<TimeOffTypeResponse>>> getAllTimeOffTypes() {
        List<TimeOffTypeResponse> responses = service.getAllTimeOffTypes();
        return ResponseEntity.ok(ApiResponse.success("Time off types retrieved successfully", responses));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TimeOffTypeResponse>> getTimeOffTypeById(@PathVariable Long id) {
        TimeOffTypeResponse response = service.getTimeOffTypeById(id);
        return ResponseEntity.ok(ApiResponse.success("Time off type retrieved successfully", response));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<TimeOffTypeResponse>> createTimeOffType(
            @Valid @RequestBody TimeOffTypeRequest request) {
        TimeOffTypeResponse response = service.createTimeOffType(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Time off type created successfully", response));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TimeOffTypeResponse>> updateTimeOffType(
            @PathVariable Long id,
            @Valid @RequestBody TimeOffTypeRequest request) {
        TimeOffTypeResponse response = service.updateTimeOffType(id, request);
        return ResponseEntity.ok(ApiResponse.success("Time off type updated successfully", response));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTimeOffType(@PathVariable Long id) {
        service.deleteTimeOffType(id);
        return ResponseEntity.ok(ApiResponse.success("Time off type deleted successfully", null));
    }
}
