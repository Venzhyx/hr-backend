package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.PayrollPeriod;
import com.projek.hr_backend.model.PayrollPeriodStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollPeriodRepository extends JpaRepository<PayrollPeriod, Long> {

    boolean existsByMonthAndYear(int month, int year);

    Optional<PayrollPeriod> findByMonthAndYear(int month, int year);

    List<PayrollPeriod> findAllByOrderByYearDescMonthDesc();

    List<PayrollPeriod> findByStatus(PayrollPeriodStatus status);
}
