package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.AttendanceSettingsRequest;
import com.projek.hr_backend.dto.AttendanceSettingsResponse;
import com.projek.hr_backend.model.AttendanceMode;
import com.projek.hr_backend.model.AttendanceSettings;
import com.projek.hr_backend.model.ExtraHoursValidation;
import com.projek.hr_backend.repository.AttendanceSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AttendanceSettingsService {

    private final AttendanceSettingsRepository repository;

    // ─── Read ─────────────────────────────────────────────────────────────────

    public AttendanceSettingsResponse getSettings() {
        return repository.findFirstByOrderByIdAsc()
                .map(this::mapToResponse)
                .orElseGet(this::createDefaultResponse);
    }

    /**
     * Convenience method untuk service lain (CheckInService, Scheduler).
     * Fallback ke OFFLINE jika belum ada row settings.
     */
    @Transactional(readOnly = true)
    public AttendanceMode getMode() {
        return repository.findFirstByOrderByIdAsc()
                .map(s -> s.getMode() != null ? s.getMode() : AttendanceMode.OFFLINE)
                .orElse(AttendanceMode.OFFLINE);
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    @Transactional
    public AttendanceSettingsResponse updateSettings(AttendanceSettingsRequest request) {
        AttendanceSettings settings = repository.findFirstByOrderByIdAsc()
                .orElseGet(() -> {
                    AttendanceSettings s = new AttendanceSettings();
                    s.setCheckInTime(LocalTime.of(8, 0));
                    s.setCheckOutTime(LocalTime.of(17, 0));
                    s.setWfoRadius(100);
                    s.setWfhRadius(100);
                    s.setMode(AttendanceMode.OFFLINE);
                    return s;
                });

        settings.setToleranceTimeInFavorOfEmployee(request.getToleranceTimeInFavorOfEmployee());
        settings.setExtraHoursValidation(request.getExtraHoursValidation());

        if (request.getCheckInTime() != null)   settings.setCheckInTime(request.getCheckInTime());
        if (request.getCheckOutTime() != null)  settings.setCheckOutTime(request.getCheckOutTime());
        if (request.getOfficeLatitude() != null)  settings.setOfficeLatitude(request.getOfficeLatitude());
        if (request.getOfficeLongitude() != null) settings.setOfficeLongitude(request.getOfficeLongitude());
        if (request.getWfoRadius() != null)     settings.setWfoRadius(request.getWfoRadius());
        if (request.getWfhRadius() != null)     settings.setWfhRadius(request.getWfhRadius());
        if (request.getMode() != null)          settings.setMode(request.getMode());

        return mapToResponse(repository.save(settings));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private AttendanceSettingsResponse createDefaultResponse() {
        return new AttendanceSettingsResponse(
            null,
            0,
            ExtraHoursValidation.APPROVED_BY_MANAGER,
            LocalTime.of(8, 0),
            LocalTime.of(17, 0),
            null,
            null,
            100,
            100,
            AttendanceMode.OFFLINE,
            null,
            null
        );
    }

    private AttendanceSettingsResponse mapToResponse(AttendanceSettings s) {
        return new AttendanceSettingsResponse(
            s.getId(),
            s.getToleranceTimeInFavorOfEmployee(),
            s.getExtraHoursValidation(),
            s.getCheckInTime()  != null ? s.getCheckInTime()  : LocalTime.of(8, 0),
            s.getCheckOutTime() != null ? s.getCheckOutTime() : LocalTime.of(17, 0),
            s.getOfficeLatitude(),
            s.getOfficeLongitude(),
            s.getWfoRadius() != null ? s.getWfoRadius() : 100,
            s.getWfhRadius() != null ? s.getWfhRadius() : 100,
            s.getMode() != null ? s.getMode() : AttendanceMode.OFFLINE,
            s.getCreatedAt(),
            s.getUpdatedAt()
        );
    }
}
