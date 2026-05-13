package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.EmployeeAssurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeAssuranceRepository extends JpaRepository<EmployeeAssurance, Long> {
    List<EmployeeAssurance> findByEmployeeId(Long employeeId);
    
    @Modifying
    @Query("DELETE FROM EmployeeAssurance ea WHERE ea.employee.id = :employeeId")
    void deleteByEmployeeId(Long employeeId);
}
