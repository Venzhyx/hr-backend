package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.OvertimeApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OvertimeApprovalRepository extends JpaRepository<OvertimeApproval, Long> {
    List<OvertimeApproval> findByOvertimeIdOrderBySequenceAsc(Long overtimeId);
    Optional<OvertimeApproval> findByOvertimeIdAndApproverId(Long overtimeId, Long approverId);
}
