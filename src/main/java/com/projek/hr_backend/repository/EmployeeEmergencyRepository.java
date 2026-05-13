package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.EmployeeEmergency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeEmergencyRepository extends JpaRepository<EmployeeEmergency, Long> {
    Optional<EmployeeEmergency> findByEmployeeId(Long employeeId);
    boolean existsByEmployeeId(Long employeeId);
    
    @Modifying
    @Query("DELETE FROM EmployeeEmergency e WHERE e.employee.id = :employeeId")
    void deleteByEmployeeId(Long employeeId);
}
