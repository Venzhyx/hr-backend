package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.LocationValidationResponse;
import com.projek.hr_backend.model.AttendanceSettings;
import com.projek.hr_backend.model.AttendanceType;
import com.projek.hr_backend.model.Employee;
import com.projek.hr_backend.repository.AttendanceSettingsRepository;
import com.projek.hr_backend.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationValidationService {

    private final AttendanceSettingsRepository settingsRepository;

    public LocationValidationResponse validateLocation(Employee employee,
                                                       Double latitude,
                                                       Double longitude,
                                                       AttendanceType attendanceType) {
        // GPS null → skip validation, dianggap valid
        if (latitude == null || longitude == null) {
            return new LocationValidationResponse(true, 0, 0, "GPS tidak tersedia, validasi dilewati");
        }

        // WFA → selalu valid
        if (attendanceType == AttendanceType.WFA) {
            return new LocationValidationResponse(true, 0, 0, "WFA tidak memerlukan validasi lokasi");
        }

        AttendanceSettings settings = settingsRepository.findFirstByOrderByIdAsc().orElse(null);

        if (attendanceType == AttendanceType.WFO) {
            // Validasi radius kantor
            if (settings == null || settings.getOfficeLatitude() == null || settings.getOfficeLongitude() == null) {
                return new LocationValidationResponse(true, 0, 0, "Lokasi kantor belum dikonfigurasi, validasi dilewati");
            }

            double radius = settings.getWfoRadius() != null ? settings.getWfoRadius() : 100.0;
            double distance = DistanceCalculator.calculate(
                    latitude, longitude,
                    settings.getOfficeLatitude(), settings.getOfficeLongitude()
            );

            boolean isValid = distance <= radius;
            String message = isValid
                    ? String.format("Lokasi valid, jarak ke kantor: %.0f meter", distance)
                    : String.format("Lokasi di luar radius kantor (jarak: %.0f meter, max: %.0f meter)", distance, radius);

            return new LocationValidationResponse(isValid, distance, radius, message);
        }

        if (attendanceType == AttendanceType.WFH) {
            // Validasi radius rumah
            if (employee.getHomeLatitude() == null || employee.getHomeLongitude() == null) {
                return new LocationValidationResponse(true, 0, 0, "Lokasi rumah karyawan belum dikonfigurasi, validasi dilewati");
            }

            double radius = (settings != null && settings.getWfhRadius() != null) ? settings.getWfhRadius() : 100.0;
            double distance = DistanceCalculator.calculate(
                    latitude, longitude,
                    employee.getHomeLatitude(), employee.getHomeLongitude()
            );

            boolean isValid = distance <= radius;
            String message = isValid
                    ? String.format("Lokasi valid, jarak ke rumah: %.0f meter", distance)
                    : String.format("Lokasi di luar radius rumah (jarak: %.0f meter, max: %.0f meter)", distance, radius);

            return new LocationValidationResponse(isValid, distance, radius, message);
        }

        return new LocationValidationResponse(true, 0, 0, "Validasi tidak diperlukan");
    }
}
