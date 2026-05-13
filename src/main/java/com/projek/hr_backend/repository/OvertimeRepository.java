package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.ApprovalStatus;
import com.projek.hr_backend.model.Overtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OvertimeRepository extends JpaRepository<Overtime, Long> {
    List<Overtime> findByEmployeeId(Long employeeId);
    List<Overtime> findByStatus(ApprovalStatus status);

    @Query("SELECT COALESCE(SUM(o.totalHours), 0) FROM Overtime o " +
           "WHERE o.employee.id = :employeeId " +
           "AND o.status = 'APPROVED' " +
           "AND MONTH(o.date) = :month " +
           "AND YEAR(o.date) = :year")
    Double getTotalApprovedHoursByEmployeeAndMonth(Long employeeId, int month, int year);

    boolean existsByEmployeeIdAndDateAndStatusNot(Long employeeId, LocalDate date, ApprovalStatus status);
}
