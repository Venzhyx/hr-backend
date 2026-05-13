package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.AssuranceMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssuranceMasterRepository extends JpaRepository<AssuranceMaster, Long> {
}
