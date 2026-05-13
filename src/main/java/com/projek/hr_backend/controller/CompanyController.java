package com.projek.hr_backend.controller;

import com.projek.hr_backend.dto.ApiResponse;
import com.projek.hr_backend.dto.CompanyRequest;
import com.projek.hr_backend.dto.CompanyResponse;
import com.projek.hr_backend.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(
            @RequestBody CompanyRequest request
    ) {
        CompanyResponse response = companyService.createCompany(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Company created successfully", response));
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(
            @PathVariable Long id,
            @RequestBody CompanyRequest request
    ) {
        CompanyResponse response = companyService.updateCompany(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Company updated successfully", response)
        );
    }

    // ================= GET =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Company retrieved successfully", companyService.getCompany(id))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> getAllCompanies() {
        return ResponseEntity.ok(
                ApiResponse.success("Companies retrieved successfully", companyService.getAllCompanies())
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok(
                ApiResponse.success("Company deleted successfully", null)
        );
    }
}