package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.EmployeeSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeSalaryRepository extends JpaRepository<EmployeeSalary, Long> {

    @Query("SELECT es FROM EmployeeSalary es JOIN FETCH es.employee WHERE es.employee.id = :employeeId AND es.isActive = true")
    Optional<EmployeeSalary> findByEmployeeIdAndIsActiveTrue(@Param("employeeId") Long employeeId);

    @Query("SELECT es FROM EmployeeSalary es JOIN FETCH es.employee WHERE es.employee.id = :employeeId")
    List<EmployeeSalary> findByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT es FROM EmployeeSalary es JOIN FETCH es.employee WHERE es.id = :id")
    Optional<EmployeeSalary> findByIdWithEmployee(@Param("id") Long id);

    boolean existsByEmployeeIdAndIsActiveTrue(Long employeeId);
}
