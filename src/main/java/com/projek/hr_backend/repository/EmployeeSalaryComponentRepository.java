package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.EmployeeSalaryComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeSalaryComponentRepository extends JpaRepository<EmployeeSalaryComponent, Long> {
    List<EmployeeSalaryComponent> findByEmployeeSalaryId(Long employeeSalaryId);
    void deleteByEmployeeSalaryId(Long employeeSalaryId);
}
