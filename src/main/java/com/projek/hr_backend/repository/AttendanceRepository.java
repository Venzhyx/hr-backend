package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByEmployeeId(Long employeeId);
    boolean existsByEmployeeIdAndDate(Long employeeId, LocalDate date);
    Optional<Attendance> findByEmployeeIdAndDate(Long employeeId, LocalDate date);

    /** Ambil semua attendance employee dalam rentang tanggal periode payroll. */
    @Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId AND a.date BETWEEN :startDate AND :endDate")
    List<Attendance> findByEmployeeIdAndDateBetween(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /** Hitung jumlah hari dengan status tertentu dalam periode. */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee.id = :employeeId AND a.status = :status AND a.date BETWEEN :startDate AND :endDate")
    long countByEmployeeIdAndStatusAndDateBetween(
            @Param("employeeId") Long employeeId,
            @Param("status") String status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
