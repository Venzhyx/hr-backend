package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.AttendanceSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendanceSettingsRepository extends JpaRepository<AttendanceSettings, Long> {
    Optional<AttendanceSettings> findFirstByOrderByIdAsc();
}
