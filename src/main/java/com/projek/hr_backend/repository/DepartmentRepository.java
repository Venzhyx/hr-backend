package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    List<Department> findByCompanyId(Long companyId);
    
    List<Department> findByParentDepartmentIsNull();
    
    List<Department> findByCompanyIdAndParentDepartmentIsNull(Long companyId);
    
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId")
    long countEmployeesByDepartmentId(@Param("departmentId") Long departmentId);
}
