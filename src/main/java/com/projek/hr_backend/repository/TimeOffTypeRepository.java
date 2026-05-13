package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.TimeOffStatus;
import com.projek.hr_backend.model.TimeOffType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeOffTypeRepository extends JpaRepository<TimeOffType, Long> {
    List<TimeOffType> findByStatus(TimeOffStatus status);
}
