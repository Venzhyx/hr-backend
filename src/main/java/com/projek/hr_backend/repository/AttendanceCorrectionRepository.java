package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.AttendanceCorrection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceCorrectionRepository extends JpaRepository<AttendanceCorrection, Long> {
    List<AttendanceCorrection> findByStatus(String status);
    List<AttendanceCorrection> findByEmployeeId(Long employeeId);
}
