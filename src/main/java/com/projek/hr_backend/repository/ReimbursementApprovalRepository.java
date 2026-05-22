package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.ApprovalStatus;
import com.projek.hr_backend.model.ReimbursementApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReimbursementApprovalRepository extends JpaRepository<ReimbursementApproval, Long> {

    List<ReimbursementApproval> findByReimbursementId(Long reimbursementId);

    List<ReimbursementApproval> findByReimbursementIdOrderBySequenceAsc(Long reimbursementId);

    long countByReimbursementIdAndStatus(Long reimbursementId, ApprovalStatus status);

    List<ReimbursementApproval> findByApproverId(Long approverId);

    /** Cek apakah approver tertentu punya giliran di reimbursement ini. */
    Optional<ReimbursementApproval> findByReimbursementIdAndApproverId(
            Long reimbursementId, Long approverId);

    void deleteByReimbursementId(Long reimbursementId);
}
