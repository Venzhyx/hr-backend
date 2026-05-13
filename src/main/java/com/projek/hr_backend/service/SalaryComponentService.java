package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.SalaryComponentRequest;
import com.projek.hr_backend.dto.SalaryComponentResponse;
import com.projek.hr_backend.exception.BadRequestException;
import com.projek.hr_backend.exception.ResourceNotFoundException;
import com.projek.hr_backend.model.SalaryComponent;
import com.projek.hr_backend.repository.SalaryComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryComponentService {

    private final SalaryComponentRepository repository;

    @Transactional
    public SalaryComponentResponse create(SalaryComponentRequest request) {
        if (repository.existsByCode(request.getCode())) {
            throw new BadRequestException("Salary component with code '" + request.getCode() + "' already exists");
        }

        SalaryComponent component = new SalaryComponent();
        component.setCode(request.getCode());
        component.setName(request.getName());
        component.setDescription(request.getDescription());
        component.setType(request.getType());
        component.setCalculationType(request.getCalculationType());
        component.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        return mapToResponse(repository.save(component));
    }

    public List<SalaryComponentResponse> getAll() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<SalaryComponentResponse> getAllActive() {
        return repository.findByIsActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SalaryComponentResponse update(Long id, SalaryComponentRequest request) {
        SalaryComponent component = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary component not found"));

        // Validasi duplikat code, kecuali milik dirinya sendiri
        if (repository.existsByCodeAndIdNot(request.getCode(), id)) {
            throw new BadRequestException("Salary component with code '" + request.getCode() + "' already exists");
        }

        component.setCode(request.getCode());
        component.setName(request.getName());
        component.setDescription(request.getDescription());
        component.setType(request.getType());
        component.setCalculationType(request.getCalculationType());
        if (request.getIsActive() != null) {
            component.setIsActive(request.getIsActive());
        }
        return mapToResponse(repository.save(component));
    }

    @Transactional
    public void deactivate(Long id) {
        SalaryComponent component = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary component not found"));
        component.setIsActive(false);
        repository.save(component);
    }

    private SalaryComponentResponse mapToResponse(SalaryComponent c) {
        return new SalaryComponentResponse(
                c.getId(), c.getCode(), c.getName(), c.getDescription(),
                c.getType(), c.getCalculationType(), c.getIsActive(),
                c.getCreatedAt(), c.getUpdatedAt()
        );
    }
}
