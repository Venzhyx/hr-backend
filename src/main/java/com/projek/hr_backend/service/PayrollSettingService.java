package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.PayrollSettingRequest;
import com.projek.hr_backend.dto.PayrollSettingResponse;
import com.projek.hr_backend.model.PayrollSetting;
import com.projek.hr_backend.repository.PayrollSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollSettingService {

    public static final String KEY_ABSENT_DEDUCTION = "ABSENT_DEDUCTION_PER_DAY";
    public static final String KEY_LATE_DEDUCTION   = "LATE_DEDUCTION_PER_DAY";

    private static final BigDecimal DEFAULT_ABSENT_DEDUCTION = new BigDecimal("100000");
    private static final BigDecimal DEFAULT_LATE_DEDUCTION   = new BigDecimal("25000");

    private final PayrollSettingRepository repository;

    // ─── Seed default rows on startup ────────────────────────────────────────

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedDefaults() {
        seedIfAbsent(KEY_ABSENT_DEDUCTION, DEFAULT_ABSENT_DEDUCTION,
                "Potongan per hari untuk karyawan yang tidak hadir (ABSENT)");
        seedIfAbsent(KEY_LATE_DEDUCTION, DEFAULT_LATE_DEDUCTION,
                "Potongan per hari untuk karyawan yang terlambat (LATE)");
        log.info("PayrollSetting defaults seeded.");
    }

    private void seedIfAbsent(String key, BigDecimal defaultValue, String description) {
        if (!repository.existsById(key)) {
            PayrollSetting setting = new PayrollSetting();
            setting.setSettingKey(key);
            setting.setSettingValue(defaultValue);
            setting.setDescription(description);
            repository.save(setting);
            log.info("Seeded payroll setting: {} = {}", key, defaultValue);
        }
    }

    // ─── Read ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PayrollSettingResponse getSettings() {
        BigDecimal absent = getValue(KEY_ABSENT_DEDUCTION, DEFAULT_ABSENT_DEDUCTION);
        BigDecimal late   = getValue(KEY_LATE_DEDUCTION,   DEFAULT_LATE_DEDUCTION);

        // updatedAt = yang paling baru di antara kedua row
        LocalDateTime updatedAt = repository.findAll().stream()
                .filter(s -> s.getSettingKey().equals(KEY_ABSENT_DEDUCTION)
                          || s.getSettingKey().equals(KEY_LATE_DEDUCTION))
                .map(PayrollSetting::getUpdatedAt)
                .filter(t -> t != null)
                .max(Comparator.naturalOrder())
                .orElse(null);

        return new PayrollSettingResponse(absent, late, updatedAt);
    }

    /**
     * Convenience method untuk PayrollRunService — ambil nilai satu key.
     * Fallback ke default jika row belum ada.
     */
    @Transactional(readOnly = true)
    public BigDecimal getValue(String key, BigDecimal fallback) {
        return repository.findById(key)
                .map(PayrollSetting::getSettingValue)
                .orElse(fallback);
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    @Transactional
    public PayrollSettingResponse updateSettings(PayrollSettingRequest request) {
        upsert(KEY_ABSENT_DEDUCTION, request.getAbsentDeductionPerDay());
        upsert(KEY_LATE_DEDUCTION,   request.getLateDeductionPerDay());
        log.info("PayrollSettings updated: absent={} late={}",
                request.getAbsentDeductionPerDay(), request.getLateDeductionPerDay());
        return getSettings();
    }

    private void upsert(String key, BigDecimal value) {
        PayrollSetting setting = repository.findById(key)
                .orElseGet(() -> {
                    PayrollSetting s = new PayrollSetting();
                    s.setSettingKey(key);
                    return s;
                });
        setting.setSettingValue(value);
        repository.save(setting);
    }
}
