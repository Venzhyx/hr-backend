package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.ApprovalApprover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalApproverRepository extends JpaRepository<ApprovalApprover, Long> {
    List<ApprovalApprover> findAllByOrderByApprovalOrderAsc();
}
