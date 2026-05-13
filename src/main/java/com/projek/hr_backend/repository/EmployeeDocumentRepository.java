package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> {
    Optional<EmployeeDocument> findByEmployeeId(Long employeeId);
    
    @Modifying
    @Query("DELETE FROM EmployeeDocument ed WHERE ed.employee.id = :employeeId")
    void deleteByEmployeeId(Long employeeId);
}
