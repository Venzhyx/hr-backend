package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.ApprovalStatus;
import com.projek.hr_backend.model.ReimbursementApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReimbursementApprovalRepository extends JpaRepository<ReimbursementApproval, Long> {
    List<ReimbursementApproval> findByReimbursementId(Long reimbursementId);
    long countByReimbursementIdAndStatus(Long reimbursementId, ApprovalStatus status);
    List<ReimbursementApproval> findByApproverId(Long approverId);
    void deleteByReimbursementId(Long reimbursementId);
}
