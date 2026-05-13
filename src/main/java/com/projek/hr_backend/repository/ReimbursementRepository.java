package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.Reimbursement;
import com.projek.hr_backend.model.ReimbursementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReimbursementRepository extends JpaRepository<Reimbursement, Long> {
    List<Reimbursement> findByEmployeeId(Long employeeId);
    List<Reimbursement> findByStatus(ReimbursementStatus status);
}
