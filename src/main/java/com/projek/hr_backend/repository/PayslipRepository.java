package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayslipRepository extends JpaRepository<Payslip, Long> {

    @Query("SELECT p FROM Payslip p JOIN FETCH p.payrollPeriod JOIN FETCH p.employee WHERE p.employee.id = :employeeId ORDER BY p.payrollPeriod.year DESC, p.payrollPeriod.month DESC")
    List<Payslip> findByEmployeeIdWithPeriod(@Param("employeeId") Long employeeId);

    @Query("SELECT p FROM Payslip p JOIN FETCH p.payrollPeriod JOIN FETCH p.employee WHERE p.id = :id")
    Optional<Payslip> findByIdWithDetails(@Param("id") Long id);

    /** Fetch lengkap dengan department dan company — dipakai untuk PDF generation. */
    @Query("""
        SELECT p FROM Payslip p
        JOIN FETCH p.payrollPeriod
        JOIN FETCH p.employee e
        JOIN FETCH e.department d
        LEFT JOIN FETCH e.company
        WHERE p.id = :id
        """)
    Optional<Payslip> findByIdForExport(@Param("id") Long id);

    /** Fetch semua payslip dalam satu periode dengan employee + department — dipakai untuk Excel. */
    @Query("""
        SELECT p FROM Payslip p
        JOIN FETCH p.payrollPeriod
        JOIN FETCH p.employee e
        JOIN FETCH e.department
        WHERE p.payrollPeriod.id = :periodId
        ORDER BY e.name ASC
        """)
    List<Payslip> findByPayrollPeriodIdForExport(@Param("periodId") Long periodId);

    @Query("SELECT p FROM Payslip p JOIN FETCH p.payrollPeriod JOIN FETCH p.employee WHERE p.payrollPeriod.id = :periodId")
    List<Payslip> findByPayrollPeriodId(@Param("periodId") Long periodId);

    boolean existsByPayrollPeriodIdAndEmployeeId(Long payrollPeriodId, Long employeeId);
}
