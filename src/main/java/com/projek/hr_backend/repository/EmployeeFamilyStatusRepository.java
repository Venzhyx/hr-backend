package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.EmployeeFamilyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeFamilyStatusRepository extends JpaRepository<EmployeeFamilyStatus, Long> {
    Optional<EmployeeFamilyStatus> findByEmployeeId(Long employeeId);
    boolean existsByEmployeeId(Long employeeId);
    
    @Modifying
    @Query("DELETE FROM EmployeeFamilyStatus e WHERE e.employee.id = :employeeId")
    void deleteByEmployeeId(Long employeeId);
}
