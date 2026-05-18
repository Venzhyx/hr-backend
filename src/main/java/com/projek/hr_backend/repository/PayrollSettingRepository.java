package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.PayrollSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayrollSettingRepository extends JpaRepository<PayrollSetting, String> {
    // PK adalah settingKey (String), findById sudah cukup untuk lookup per-key
}
