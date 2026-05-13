package com.projek.hr_backend.controller;

import com.projek.hr_backend.dto.ApiResponse;
import com.projek.hr_backend.model.AssuranceMaster;
import com.projek.hr_backend.model.Bank;
import com.projek.hr_backend.repository.AssuranceMasterRepository;
import com.projek.hr_backend.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/master")
@RequiredArgsConstructor
public class MasterDataController {
    
    private final BankRepository bankRepository;
    private final AssuranceMasterRepository assuranceMasterRepository;
    
    @GetMapping("/banks")
    public ResponseEntity<ApiResponse<List<Bank>>> getAllBanks() {
        List<Bank> banks = bankRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Banks retrieved successfully", banks));
    }
    
    @GetMapping("/assurances")
    public ResponseEntity<ApiResponse<List<AssuranceMaster>>> getAllAssurances() {
        List<AssuranceMaster> assurances = assuranceMasterRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Assurances retrieved successfully", assurances));
    }
}
