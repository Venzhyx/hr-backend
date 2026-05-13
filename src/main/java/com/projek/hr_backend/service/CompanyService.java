package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.CompanyRequest;
import com.projek.hr_backend.dto.CompanyResponse;
import com.projek.hr_backend.exception.ResourceNotFoundException;
import com.projek.hr_backend.model.Company;
import com.projek.hr_backend.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    // ================= CREATE =================
    @Transactional
    public CompanyResponse createCompany(CompanyRequest request) {
        Company company = new Company();
        mapToEntity(request, company);
        Company saved = companyRepository.save(company);
        return mapToResponse(saved);
    }

    // ================= UPDATE =================
    @Transactional
    public CompanyResponse updateCompany(Long id, CompanyRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

        mapToEntity(request, company);
        Company updated = companyRepository.save(company);
        return mapToResponse(updated);
    }

    // ================= GET =================
    public CompanyResponse getCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        return mapToResponse(company);
    }

    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Company not found with id: " + id);
        }
        companyRepository.deleteById(id);
    }

    // ================= MAPPERS =================
    private void mapToEntity(CompanyRequest request, Company company) {
        company.setLogo(request.getLogo());
        company.setCompanyName(request.getCompanyName());
        company.setAddress(request.getAddress());
        company.setCountry(request.getCountry());
        company.setCity(request.getCity());
        company.setZip(request.getZip());
        company.setCompanyId(request.getCompanyId());
        company.setPhone(request.getPhone());
        company.setEmail(request.getEmail());
        company.setWebsite(request.getWebsite());
        company.setLatitude(request.getLatitude());
        company.setLongitude(request.getLongitude());
        company.setFormattedAddress(request.getFormattedAddress());
    }

    private CompanyResponse mapToResponse(Company company) {
        CompanyResponse response = new CompanyResponse();

        response.setId(company.getId());
        response.setLogo(company.getLogo());
        response.setCompanyName(company.getCompanyName());
        response.setAddress(company.getAddress());
        response.setCountry(company.getCountry());
        response.setCity(company.getCity());
        response.setZip(company.getZip());
        response.setCompanyId(company.getCompanyId());
        response.setPhone(company.getPhone());
        response.setEmail(company.getEmail());
        response.setWebsite(company.getWebsite());
        response.setLatitude(company.getLatitude());
        response.setLongitude(company.getLongitude());
        response.setFormattedAddress(company.getFormattedAddress());
        response.setCreatedAt(company.getCreatedAt());
        response.setUpdatedAt(company.getUpdatedAt());

        return response;
    }
}