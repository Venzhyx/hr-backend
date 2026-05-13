package com.projek.hr_backend.controller;

import com.projek.hr_backend.dto.ApiResponse;
import com.projek.hr_backend.dto.CheckInResponse;
import com.projek.hr_backend.dto.CheckOutResponse;
import com.projek.hr_backend.dto.LocationValidationResponse;
import com.projek.hr_backend.model.Attendance;
import com.projek.hr_backend.model.AttendanceType;
import com.projek.hr_backend.model.Employee;
import com.projek.hr_backend.repository.AttendanceRepository;
import com.projek.hr_backend.repository.EmployeeRepository;
import com.projek.hr_backend.service.AttendanceService;
import com.projek.hr_backend.service.CheckInService;
import com.projek.hr_backend.service.LocationValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AttendanceController {

    private final AttendanceService service;
    private final CheckInService checkInService;
    private final AttendanceRepository attendanceRepository;
    private final LocationValidationService locationValidationService;
    private final EmployeeRepository employeeRepository;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadExcel(
            @RequestParam("file") MultipartFile file) throws IOException {
        service.importExcel(file);
        return ResponseEntity.ok(new ApiResponse<>(true, "Upload success", null));
    }

    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<CheckInResponse>> checkIn(
            @RequestParam Long employeeId,
            @RequestParam MultipartFile photo,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) String attendanceType,
            @RequestParam(required = false) String workType,
            @RequestParam(required = false) Double accuracy,
            @RequestParam(required = false) String deviceInfo,
            jakarta.servlet.http.HttpServletRequest httpRequest) throws IOException {
        String type = attendanceType != null ? attendanceType : workType;
        if (type == null) type = "WFO";
        CheckInResponse response = checkInService.checkIn(
                employeeId, photo, latitude, longitude, type,
                accuracy, deviceInfo, httpRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "Check-in berhasil", response));
    }

    @PostMapping("/check-out")
    public ResponseEntity<ApiResponse<CheckOutResponse>> checkOut(
            @RequestParam Long employeeId,
            @RequestParam MultipartFile photo,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) throws IOException {
        CheckOutResponse response = checkInService.checkOut(employeeId, photo, latitude, longitude);
        return ResponseEntity.ok(new ApiResponse<>(true, "Check-out berhasil", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllAttendances() {
        List<Attendance> attendances = service.getAllAttendances();
        return ResponseEntity.ok(new ApiResponse<>(true, "Attendances retrieved successfully", toDto(attendances)));
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAttendancesByEmployee(
            @PathVariable Long id) {
        List<Attendance> attendances = service.getAttendancesByEmployee(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Attendances retrieved successfully", toDto(attendances)));
    }

    @GetMapping("/validate-location")
    public ResponseEntity<ApiResponse<LocationValidationResponse>> validateLocation(
            @RequestParam Long employeeId,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam String attendanceType) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        LocationValidationResponse result = locationValidationService.validateLocation(
                employee, latitude, longitude,
                AttendanceType.valueOf(attendanceType.toUpperCase()));
        return ResponseEntity.ok(new ApiResponse<>(true, result.getMessage(), result));
    }

    @GetMapping("/today/{employeeId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTodayAttendance(
            @PathVariable Long employeeId) {
        return attendanceRepository.findByEmployeeIdAndDate(employeeId, java.time.LocalDate.now())
                .map(att -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("hasCheckedIn",  att.getCheckIn() != null);
                    map.put("hasCheckedOut", att.getCheckOut() != null);
                    map.put("checkIn",       att.getCheckIn());
                    map.put("checkOut",      att.getCheckOut());
                    map.put("status",        att.getStatus());
                    map.put("attendanceType",att.getAttendanceType());
                    map.put("source",        att.getSource());
                    return ResponseEntity.ok(new ApiResponse<>(true, "Today attendance found", map));
                })
                .orElseGet(() -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("hasCheckedIn",  false);
                    map.put("hasCheckedOut", false);
                    map.put("checkIn",       null);
                    map.put("checkOut",      null);
                    map.put("status",        null);
                    map.put("attendanceType",null);
                    map.put("source",        null);
                    return ResponseEntity.ok(new ApiResponse<>(true, "No attendance today", map));
                });
    }

    private List<Map<String, Object>> toDto(List<Attendance> attendances) {
        return attendances.stream().map(att -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id",             att.getId());
            map.put("date",           att.getDate());
            map.put("checkIn",        att.getCheckIn());
            map.put("checkOut",       att.getCheckOut());
            map.put("status",         att.getStatus());
            map.put("employeeCode",   att.getEmployeeCode());
            map.put("employeeName",   att.getEmployeeName());
            map.put("photoPath",          att.getPhotoPath());
            map.put("latitude",            att.getLatitude());
            map.put("longitude",           att.getLongitude());
            map.put("checkOutPhotoPath",   att.getCheckOutPhotoPath());
            map.put("checkOutLatitude",    att.getCheckOutLatitude());
            map.put("checkOutLongitude",   att.getCheckOutLongitude());
            map.put("attendanceType",      att.getAttendanceType());
            map.put("source",              att.getSource());
            map.put("createdAt",      att.getCreatedAt());

            if (att.getEmployee() != null) {
                Map<String, Object> emp = new LinkedHashMap<>();
                emp.put("id",   att.getEmployee().getId());
                emp.put("name", att.getEmployee().getName());
                map.put("employee", emp);
            }

            return map;
        }).collect(Collectors.toList());
    }
}
