package com.projek.hr_backend.controller;

import com.projek.hr_backend.dto.ApiResponse;
import com.projek.hr_backend.dto.EmployeeCompleteRequest;
import com.projek.hr_backend.dto.EmployeeCompleteResponse;
import com.projek.hr_backend.service.EmployeeCompleteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeCompleteController {
    
    private final EmployeeCompleteService employeeService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeCompleteResponse>> createEmployee(@Valid @RequestBody EmployeeCompleteRequest request) {
        EmployeeCompleteResponse response = employeeService.createEmployee(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created successfully", response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeCompleteResponse>> getEmployeeById(@PathVariable Long id) {
        EmployeeCompleteResponse response = employeeService.getEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee retrieved successfully", response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeCompleteResponse>>> getAllEmployees() {
        List<EmployeeCompleteResponse> responses = employeeService.getAllEmployees();
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", responses));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeCompleteResponse>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeCompleteRequest request) {
        EmployeeCompleteResponse response = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", response));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deleted successfully", null));
    }
}
