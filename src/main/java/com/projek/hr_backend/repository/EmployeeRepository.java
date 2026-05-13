package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    List<Employee> findByDepartmentId(Long departmentId);
    
    @Query("SELECT FUNCTION('TO_CHAR', e.joinDate, 'YYYY-MM') as month, COUNT(e) as count " +
           "FROM Employee e " +
           "GROUP BY FUNCTION('TO_CHAR', e.joinDate, 'YYYY-MM') " +
           "ORDER BY month DESC")
    List<Object[]> getMonthlyJoinStatistics();
    
    long countByManagerId(Long managerId);
    
    long countByCoachId(Long coachId);
    
    @Query("SELECT COUNT(d) FROM Department d WHERE d.manager.id = :employeeId")
    long countDepartmentsByManagerId(Long employeeId);
    
   
}
