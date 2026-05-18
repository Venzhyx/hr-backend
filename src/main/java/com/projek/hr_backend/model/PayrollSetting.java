package com.projek.hr_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Key-value store untuk konfigurasi payroll yang bisa diubah tanpa deploy ulang.
 *
 * Row awal (di-seed saat startup):
 *   ABSENT_DEDUCTION_PER_DAY  → 100000
 *   LATE_DEDUCTION_PER_DAY    → 25000
 */
@Entity
@Table(name = "payroll_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayrollSetting {

    @Id
    @Column(name = "setting_key", nullable = false, length = 100)
    private String settingKey;

    @Column(name = "setting_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal settingValue;

    @Column(length = 255)
    private String description;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
