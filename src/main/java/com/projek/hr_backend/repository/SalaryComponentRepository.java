package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.SalaryComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryComponentRepository extends JpaRepository<SalaryComponent, Long> {
    List<SalaryComponent> findByIsActiveTrue();
    Optional<SalaryComponent> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
}
