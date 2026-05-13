package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.EmployeePrivateInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeePrivateInfoRepository extends JpaRepository<EmployeePrivateInfo, Long> {
    
    Optional<EmployeePrivateInfo> findByEmployeeId(Long employeeId);
    
    boolean existsByEmployeeId(Long employeeId);
    
    @Modifying
    @Query("DELETE FROM EmployeePrivateInfo e WHERE e.employee.id = :employeeId")
    void deleteByEmployeeId(Long employeeId);
}
