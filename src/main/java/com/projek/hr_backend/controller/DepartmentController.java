package com.projek.hr_backend.controller;

import com.projek.hr_backend.dto.ApiResponse;
import com.projek.hr_backend.dto.DepartmentRequest;
import com.projek.hr_backend.dto.DepartmentResponse;
import com.projek.hr_backend.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    
    private final DepartmentService departmentService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse response = departmentService.createDepartment(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Department created successfully", response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentById(@PathVariable Long id) {
        DepartmentResponse response = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(ApiResponse.success("Department retrieved successfully", response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getAllDepartments(
            @RequestParam(required = false) Long companyId) {
        List<DepartmentResponse> responses = companyId != null 
            ? departmentService.getDepartmentsByCompany(companyId)
            : departmentService.getAllDepartments();
        return ResponseEntity.ok(ApiResponse.success("Departments retrieved successfully", responses));
    }
    
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getDepartmentTree(
            @RequestParam(required = false) Long companyId) {
        List<DepartmentResponse> tree = departmentService.getDepartmentTree(companyId);
        return ResponseEntity.ok(ApiResponse.success("Department tree retrieved successfully", tree));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse response = departmentService.updateDepartment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Department updated successfully", response));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success("Department deleted successfully", null));
    }
    
    @GetMapping("/{id}/employee-count")
    public ResponseEntity<ApiResponse<Long>> getEmployeeCount(@PathVariable Long id) {
        long count = departmentService.getEmployeeCountByDepartment(id);
        return ResponseEntity.ok(ApiResponse.success("Employee count retrieved successfully", count));
    }
}
