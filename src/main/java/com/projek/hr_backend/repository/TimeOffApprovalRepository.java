package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.TimeOffApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimeOffApprovalRepository extends JpaRepository<TimeOffApproval, Long> {

    List<TimeOffApproval> findByTimeOffRequestId(Long timeOffRequestId);

    List<TimeOffApproval> findByTimeOffRequestIdOrderBySequenceAsc(Long timeOffRequestId);

    /** Cek apakah approver tertentu punya giliran di time-off request ini. */
    Optional<TimeOffApproval> findByTimeOffRequestIdAndApproverId(
            Long timeOffRequestId, Long approverId);

    void deleteByTimeOffRequestId(Long timeOffRequestId);
}
