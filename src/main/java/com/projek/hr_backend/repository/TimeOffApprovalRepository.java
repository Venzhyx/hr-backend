package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.TimeOffApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeOffApprovalRepository extends JpaRepository<TimeOffApproval, Long> {

    List<TimeOffApproval> findByTimeOffRequestId(Long timeOffRequestId);
    void deleteByTimeOffRequestId(Long timeOffRequestId);
}
