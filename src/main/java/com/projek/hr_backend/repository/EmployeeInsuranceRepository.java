package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.EmployeeInsurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeInsuranceRepository extends JpaRepository<EmployeeInsurance, Long> {
    List<EmployeeInsurance> findByEmployeeId(Long employeeId);
    void deleteByEmployeeId(Long employeeId);
}
