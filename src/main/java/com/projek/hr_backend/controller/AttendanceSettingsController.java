package com.projek.hr_backend.controller;

import com.projek.hr_backend.dto.ApiResponse;
import com.projek.hr_backend.dto.AttendanceSettingsRequest;
import com.projek.hr_backend.dto.AttendanceSettingsResponse;
import com.projek.hr_backend.service.AttendanceSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings/attendance")
@RequiredArgsConstructor
public class AttendanceSettingsController {
    
    private final AttendanceSettingsService service;
    
    @GetMapping
    public ResponseEntity<ApiResponse<AttendanceSettingsResponse>> getSettings() {
        AttendanceSettingsResponse response = service.getSettings();
        return ResponseEntity.ok(ApiResponse.success("Attendance settings retrieved successfully", response));
    }
    
    @PutMapping
    public ResponseEntity<ApiResponse<AttendanceSettingsResponse>> updateSettings(
            @Valid @RequestBody AttendanceSettingsRequest request) {
        AttendanceSettingsResponse response = service.updateSettings(request);
        return ResponseEntity.ok(ApiResponse.success("Attendance settings updated successfully", response));
    }
}
