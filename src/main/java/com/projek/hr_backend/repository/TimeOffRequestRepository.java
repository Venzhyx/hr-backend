package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.TimeOffRequest;
import com.projek.hr_backend.model.TimeOffRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeOffRequestRepository extends JpaRepository<TimeOffRequest, Long> {
    List<TimeOffRequest> findByEmployeeId(Long employeeId);
    List<TimeOffRequest> findByStatus(TimeOffRequestStatus status);
}
