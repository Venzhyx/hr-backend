package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.DepartmentRequest;
import com.projek.hr_backend.dto.DepartmentResponse;
import com.projek.hr_backend.exception.ResourceNotFoundException;
import com.projek.hr_backend.model.Company;
import com.projek.hr_backend.model.Department;
import com.projek.hr_backend.model.Employee;
import com.projek.hr_backend.repository.CompanyRepository;
import com.projek.hr_backend.repository.DepartmentRepository;
import com.projek.hr_backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    
    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        Department department = new Department();
        mapRequestToEntity(request, department);
        Department saved = departmentRepository.save(department);
        return mapEntityToResponse(saved);
    }
    
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        return mapEntityToResponse(department);
    }
    
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }
    
    public List<DepartmentResponse> getDepartmentsByCompany(Long companyId) {
        return departmentRepository.findByCompanyId(companyId).stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }
    
    public List<DepartmentResponse> getDepartmentTree(Long companyId) {
        List<Department> roots = companyId != null
                ? departmentRepository.findByCompanyIdAndParentDepartmentIsNull(companyId)
                : departmentRepository.findByParentDepartmentIsNull();
        
        return roots.stream()
                .map(this::mapToTreeResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        mapRequestToEntity(request, department);
        Department updated = departmentRepository.save(department);
        return mapEntityToResponse(updated);
    }
    
    @Transactional
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department not found with id: " + id);
        }
        
        long employeeCount = departmentRepository.countEmployeesByDepartmentId(id);
        if (employeeCount > 0) {
            throw new IllegalStateException("Cannot delete department. There are " + employeeCount + " employee(s) assigned.");
        }
        
        departmentRepository.deleteById(id);
    }
    
    public long getEmployeeCountByDepartment(Long id) {
        return departmentRepository.countEmployeesByDepartmentId(id);
    }
    
    private void mapRequestToEntity(DepartmentRequest request, Department department) {
        department.setDepartmentName(request.getDepartmentName());
        
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        department.setCompany(company);
        
        if (request.getParentDepartmentId() != null) {
            Department parent = departmentRepository.findById(request.getParentDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent department not found"));
            department.setParentDepartment(parent);
        }
        
        if (request.getManagerId() != null) {
            Employee manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
            department.setManager(manager);
        }
    }
    
    private DepartmentResponse mapEntityToResponse(Department department) {
        DepartmentResponse response = new DepartmentResponse();
        response.setId(department.getId());
        response.setDepartmentName(department.getDepartmentName());
        
        if (department.getParentDepartment() != null) {
            response.setParentDepartmentId(department.getParentDepartment().getId());
            response.setParentDepartmentName(department.getParentDepartment().getDepartmentName());
        }
        
        if (department.getCompany() != null) {
            response.setCompanyId(department.getCompany().getId());
            response.setCompanyName(department.getCompany().getCompanyName());
        }
        
        if (department.getManager() != null) {
            response.setManagerId(department.getManager().getId());
            response.setManagerName(department.getManager().getName());
        }
        
        response.setCreatedAt(department.getCreatedAt());
        response.setUpdatedAt(department.getUpdatedAt());
        return response;
    }
    
    private DepartmentResponse mapToTreeResponse(Department department) {
        DepartmentResponse response = mapEntityToResponse(department);
        
        List<Department> children = departmentRepository.findAll().stream()
                .filter(d -> d.getParentDepartment() != null && d.getParentDepartment().getId().equals(department.getId()))
                .collect(Collectors.toList());
        
        if (!children.isEmpty()) {
            response.setChildren(children.stream()
                    .map(this::mapToTreeResponse)
                    .collect(Collectors.toList()));
        } else {
            response.setChildren(new ArrayList<>());
        }
        
        return response;
    }
}
