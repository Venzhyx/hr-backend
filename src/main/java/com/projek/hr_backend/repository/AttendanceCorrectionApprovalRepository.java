package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.AttendanceCorrectionApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceCorrectionApprovalRepository extends JpaRepository<AttendanceCorrectionApproval, Long> {
    List<AttendanceCorrectionApproval> findByCorrectionIdOrderBySequenceAsc(Long correctionId);
    Optional<AttendanceCorrectionApproval> findByCorrectionIdAndApproverId(Long correctionId, Long approverId);
}
