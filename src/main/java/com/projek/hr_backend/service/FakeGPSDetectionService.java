package com.projek.hr_backend.service;

import com.projek.hr_backend.model.Attendance;
import com.projek.hr_backend.repository.AttendanceRepository;
import com.projek.hr_backend.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FakeGPSDetectionService {

    private final AttendanceRepository attendanceRepository;

    /**
     * Velocity Check - Deteksi teleportasi
     * Cek apakah user "teleport" dari lokasi sebelumnya
     */
    public Map<String, Object> velocityCheck(Long employeeId, Double newLat, Double newLng, LocalDateTime timestamp) {
        Map<String, Object> result = new HashMap<>();
        result.put("suspicious", false);

        if (newLat == null || newLng == null) {
            return result;
        }

        // Ambil attendance terakhir yang punya GPS
        Attendance lastAttendance = attendanceRepository.findAll().stream()
                .filter(a -> a.getEmployee().getId().equals(employeeId))
                .filter(a -> a.getLatitude() != null && a.getLongitude() != null)
                .filter(a -> a.getCheckIn() != null)
                .max((a1, a2) -> a1.getCheckIn().compareTo(a2.getCheckIn()))
                .orElse(null);

        if (lastAttendance == null) {
            return result;
        }

        // Hitung selisih waktu dalam jam
        Duration duration = Duration.between(lastAttendance.getCheckIn(), timestamp);
        double timeDiffHours = duration.toMinutes() / 60.0;

        if (timeDiffHours <= 0) {
            return result;
        }

        // Hitung jarak dalam km
        double distanceMeters = DistanceCalculator.calculate(
                lastAttendance.getLatitude(), lastAttendance.getLongitude(),
                newLat, newLng
        );
        double distanceKm = distanceMeters / 1000.0;

        // Hitung kecepatan
        double speedKmh = distanceKm / timeDiffHours;

        // Kecepatan manusia normal max ~900 km/h (pesawat)
        // Jika lebih dari itu, hampir pasti fake GPS
        boolean suspicious = speedKmh > 900;

        result.put("suspicious", suspicious);
        result.put("speedKmh", speedKmh);
        result.put("distanceKm", distanceKm);
        result.put("timeDiffHours", timeDiffHours);
        result.put("reason", suspicious ? "VELOCITY_CHECK_FAILED" : null);

        return result;
    }

    /**
     * GPS Accuracy Check
     * GPS dengan accuracy terlalu rendah (> 100 meter) bisa jadi fake
     */
    public boolean isAccuracySuspicious(Double accuracy) {
        if (accuracy == null) return false;
        return accuracy > 100.0;
    }
}
