package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.PayslipComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayslipComponentRepository extends JpaRepository<PayslipComponent, Long> {

    List<PayslipComponent> findByPayslipId(Long payslipId);

    void deleteByPayslipId(Long payslipId);
}
