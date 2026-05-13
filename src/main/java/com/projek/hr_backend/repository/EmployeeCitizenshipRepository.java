package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.EmployeeCitizenship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeCitizenshipRepository extends JpaRepository<EmployeeCitizenship, Long> {
    Optional<EmployeeCitizenship> findByEmployeeId(Long employeeId);
    boolean existsByEmployeeId(Long employeeId);
    
    @Modifying
    @Query("DELETE FROM EmployeeCitizenship e WHERE e.employee.id = :employeeId")
    void deleteByEmployeeId(Long employeeId);
}
