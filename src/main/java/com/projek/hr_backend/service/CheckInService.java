package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.CheckInResponse;
import com.projek.hr_backend.dto.CheckOutResponse;
import com.projek.hr_backend.dto.LocationValidationResponse;
import com.projek.hr_backend.exception.ResourceNotFoundException;
import com.projek.hr_backend.model.*;
import com.projek.hr_backend.repository.AttendanceRepository;
import com.projek.hr_backend.repository.AttendanceSettingsRepository;
import com.projek.hr_backend.repository.EmployeeSettingsRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckInService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeSettingsRepository employeeSettingsRepository;
    private final AttendanceSettingsRepository attendanceSettingsRepository;
    private final LocationValidationService locationValidationService;
    private final FakeGPSDetectionService fakeGPSDetectionService;

    @Value("${file.upload-dir}")
    private String baseUploadDir;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Transactional
    public CheckInResponse checkIn(Long employeeId, MultipartFile photo,
                                   Double latitude, Double longitude,
                                   String attendanceType, Double gpsAccuracy,
                                   String deviceInfo, HttpServletRequest httpRequest) throws IOException {

        // 1. Cari employee via settings
        EmployeeSettings settings = employeeSettingsRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        Employee employee = settings.getEmployee();
        if (employee == null) {
            throw new ResourceNotFoundException("Employee data not found");
        }

        // 2. Cek duplikat
        LocalDate today = LocalDate.now();
        if (attendanceRepository.existsByEmployeeIdAndDate(employeeId, today)) {
            throw new IllegalStateException("Sudah absen hari ini");
        }

        // 3. Validasi lokasi GPS
        AttendanceType type = AttendanceType.valueOf(attendanceType.toUpperCase());
        LocationValidationResponse locationResult = locationValidationService
                .validateLocation(employee, latitude, longitude, type);

        if (!locationResult.isValid()) {
            throw new IllegalStateException(locationResult.getMessage());
        }

        // 4. Fake GPS Detection
        LocalDateTime now = LocalDateTime.now();
        List<String> suspiciousReasons = new ArrayList<>();
        boolean isSuspicious = false;

        // Layer A: Velocity Check
        if (latitude != null && longitude != null) {
            Map<String, Object> velocityResult = fakeGPSDetectionService
                    .velocityCheck(employeeId, latitude, longitude, now);

            if (Boolean.TRUE.equals(velocityResult.get("suspicious"))) {
                isSuspicious = true;
                double speed = (double) velocityResult.get("speedKmh");
                suspiciousReasons.add(String.format("VELOCITY_CHECK_FAILED (%.0f km/h)", speed));
            }
        }

        // Layer B: GPS Accuracy Check
        if (fakeGPSDetectionService.isAccuracySuspicious(gpsAccuracy)) {
            isSuspicious = true;
            suspiciousReasons.add(String.format("LOW_GPS_ACCURACY (%.0f meter)", gpsAccuracy));
        }

        // 5. Ambil IP address
        String ipAddress = getClientIp(httpRequest);

        // 6. Simpan foto
        String photoPath = null;
        if (photo != null && !photo.isEmpty()) {
            photoPath = savePhoto(photo, employeeId, "checkin");
        }

        // 7. Tentukan status
        DayOfWeek day = today.getDayOfWeek();
        String status;
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            status = "OFF";
        } else {
            AttendanceSettings attSettings = attendanceSettingsRepository
                    .findFirstByOrderByIdAsc().orElse(null);

            LocalTime baseCheckIn = (attSettings != null && attSettings.getCheckInTime() != null)
                    ? attSettings.getCheckInTime() : LocalTime.of(8, 0);

            int toleranceMinutes = (attSettings != null && attSettings.getToleranceTimeInFavorOfEmployee() != null)
                    ? attSettings.getToleranceTimeInFavorOfEmployee() : 0;

            LocalTime deadline = baseCheckIn.plusMinutes(toleranceMinutes);
            status = now.toLocalTime().isAfter(deadline) ? "LATE" : "PRESENT";
        }

        // 8. Simpan attendance
        String suspiciousReasonStr = suspiciousReasons.isEmpty() ? null : String.join(", ", suspiciousReasons);

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setEmployeeCode(settings.getEmployeeIdentificationNumber());
        attendance.setEmployeeName(employee.getName());
        attendance.setDate(today);
        attendance.setCheckIn(now);
        attendance.setCheckOut(null);
        attendance.setStatus(status);
        attendance.setPhotoPath(photoPath);
        attendance.setLatitude(latitude);
        attendance.setLongitude(longitude);
        attendance.setAttendanceType(type);
        attendance.setSource(AttendanceSource.MANUAL);
        attendance.setIsLocationValidated(locationResult.isValid());
        attendance.setIsSuspicious(isSuspicious);
        attendance.setSuspiciousReason(suspiciousReasonStr);
        attendance.setIpAddress(ipAddress);
        attendance.setGpsAccuracy(gpsAccuracy);
        attendance.setDeviceInfo(deviceInfo);

        attendanceRepository.save(attendance);

        // 9. Return response
        CheckInResponse response = new CheckInResponse();
        response.setStatus("SUCCESS");
        response.setEmployeeId(employee.getId());
        response.setEmployeeName(employee.getName());
        response.setCheckInTime(now.format(TIME_FORMATTER));
        response.setAttendanceType(attendanceType.toUpperCase());
        response.setLatitude(latitude);
        response.setLongitude(longitude);
        response.setAttendanceStatus(status);
        response.setIsLocationValidated(locationResult.isValid());
        response.setDistance(locationResult.getDistance());
        response.setRadius(locationResult.getRadius());
        response.setLocationMessage(locationResult.getMessage());
        response.setIsSuspicious(isSuspicious);
        response.setSuspiciousReason(suspiciousReasonStr);

        return response;
    }

    @Transactional
    public CheckOutResponse checkOut(Long employeeId, MultipartFile photo,
                                     Double latitude, Double longitude) throws IOException {

        EmployeeSettings settings = employeeSettingsRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        Employee employee = settings.getEmployee();
        if (employee == null) throw new ResourceNotFoundException("Employee data not found");

        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(employeeId, today)
                .orElseThrow(() -> new IllegalStateException("Belum melakukan check-in hari ini"));

        if (attendance.getCheckOut() != null) {
            throw new IllegalStateException("Sudah melakukan check-out hari ini");
        }

        String photoPath = null;
        if (photo != null && !photo.isEmpty()) {
            photoPath = savePhoto(photo, employeeId, "checkout");
        }

        LocalDateTime checkOut = LocalDateTime.now();

        AttendanceSettings attSettings = attendanceSettingsRepository.findFirstByOrderByIdAsc().orElse(null);
        LocalTime expectedCheckOut = (attSettings != null && attSettings.getCheckOutTime() != null)
                ? attSettings.getCheckOutTime() : LocalTime.of(17, 0);

        String message = checkOut.toLocalTime().isBefore(expectedCheckOut)
                ? "Checkout lebih awal dari jam kerja (" + expectedCheckOut + ")"
                : "Checkout berhasil";

        attendance.setCheckOut(checkOut);
        if (photoPath != null) attendance.setCheckOutPhotoPath(photoPath);
        if (latitude != null)  attendance.setCheckOutLatitude(latitude);
        if (longitude != null) attendance.setCheckOutLongitude(longitude);
        attendanceRepository.save(attendance);

        return new CheckOutResponse(
                "SUCCESS",
                employee.getId(),
                employee.getName(),
                checkOut.format(TIME_FORMATTER),
                attendance.getStatus(),
                message
        );
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) return null;
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String savePhoto(MultipartFile photo, Long employeeId, String type) throws IOException {
        // Ambil path dari config, fallback ke folder relatif jika null
        String uploadPath = (baseUploadDir != null && !baseUploadDir.isBlank())
                ? baseUploadDir + File.separator + "attendance"
                : "uploads" + File.separator + "attendance";

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (!created) {
                throw new IOException("Gagal membuat folder upload: " + uploadDir.getAbsolutePath());
            }
        }

        System.out.println("[savePhoto] Upload dir: " + uploadDir.getAbsolutePath());
        System.out.println("[savePhoto] File size: " + photo.getSize() + " bytes");

        String fileName = System.currentTimeMillis() + "_" + employeeId + "_" + type + ".jpg";
        Path filePath = Paths.get(uploadPath, fileName);
        Files.write(filePath, photo.getBytes());

        System.out.println("[savePhoto] Saved to: " + filePath.toAbsolutePath());

        return filePath.toString();
    }
}