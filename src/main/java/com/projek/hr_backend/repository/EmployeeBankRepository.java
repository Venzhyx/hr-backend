package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.EmployeeBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeBankRepository extends JpaRepository<EmployeeBank, Long> {
    List<EmployeeBank> findByEmployeeId(Long employeeId);
    void deleteByEmployeeId(Long employeeId);
}
