package com.projek.hr_backend.service;

import com.projek.hr_backend.exception.ResourceNotFoundException;
import com.projek.hr_backend.model.Attendance;
import com.projek.hr_backend.model.Employee;
import com.projek.hr_backend.model.EmployeeSettings;
import com.projek.hr_backend.repository.AttendanceRepository;
import com.projek.hr_backend.repository.EmployeeSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeSettingsRepository employeeSettingsRepository;

    private static final DateTimeFormatter FORMATTER      = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER  = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Transactional
    public void importExcel(MultipartFile file) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell identificationCell = row.getCell(0);
                Cell workDateCell        = row.getCell(1);
                Cell checkInCell         = row.getCell(2);
                Cell checkOutCell        = row.getCell(3);

                if (identificationCell == null || workDateCell == null) continue;

                String employeeIdentificationNumber = identificationCell.getStringCellValue().trim();
                if (employeeIdentificationNumber.isEmpty()) continue;

                EmployeeSettings settings = employeeSettingsRepository
                        .findByEmployeeIdentificationNumber(employeeIdentificationNumber)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Employee not found with identification number: " + employeeIdentificationNumber));

                Employee employee = settings.getEmployee();
                if (employee == null) {
                    throw new ResourceNotFoundException(
                            "Employee data is null for identification number: " + employeeIdentificationNumber);
                }

                System.out.println("EMP CODE: " + settings.getEmployeeIdentificationNumber());
                System.out.println("EMP NAME: " + employee.getName());

                LocalDate workDate;
                if (workDateCell.getCellType() == CellType.NUMERIC) {
                    workDate = workDateCell.getLocalDateTimeCellValue().toLocalDate();
                } else {
                    String dateValue = workDateCell.getStringCellValue().trim();
                    if (dateValue.length() == 10) {
                        workDate = LocalDate.parse(dateValue);
                    } else {
                        workDate = LocalDateTime.parse(dateValue, FORMATTER).toLocalDate();
                    }
                }

                LocalDateTime checkIn = (checkInCell != null && !checkInCell.getStringCellValue().trim().isEmpty())
                        ? LocalDateTime.parse(checkInCell.getStringCellValue().trim(), FORMATTER)
                        : null;

                LocalDateTime checkOut = (checkOutCell != null && !checkOutCell.getStringCellValue().trim().isEmpty())
                        ? LocalDateTime.parse(checkOutCell.getStringCellValue().trim(), FORMATTER)
                        : null;

                DayOfWeek day = workDate.getDayOfWeek();

                // Anti duplicate: skip jika sudah ada data untuk employee + tanggal ini
                if (attendanceRepository.existsByEmployeeIdAndDate(employee.getId(), workDate)) {
                    System.out.println("[Skip] Data sudah ada: " + employeeIdentificationNumber + " - " + workDate);
                    continue;
                }

                Attendance attendance = new Attendance();
                attendance.setEmployee(employee);
                attendance.setEmployeeCode(settings.getEmployeeIdentificationNumber());
                attendance.setEmployeeName(employee.getName());
                attendance.setDate(workDate);

                if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                    attendance.setStatus("OFF");
                    attendance.setCheckIn(null);
                    attendance.setCheckOut(null);
                } else {
                    if (checkIn == null) {
                        attendance.setStatus("ABSENT");
                        attendance.setCheckIn(null);
                        attendance.setCheckOut(null);
                    } else {
                        attendance.setCheckIn(checkIn);
                        attendance.setCheckOut(checkOut);
                        if (checkIn.toLocalTime().isAfter(LocalTime.of(8, 0))) {
                            attendance.setStatus("LATE");
                        } else {
                            attendance.setStatus("PRESENT");
                        }
                    }
                }

                attendanceRepository.save(attendance);
            }
        }
    }

    public void importExcel(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            MultipartFile multipartFile = new MockMultipartFile(
                    file.getName(),
                    file.getName(),
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    fis
            );
            importExcel(multipartFile);
        }
    }

    public List<Attendance> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    public List<Attendance> getAttendancesByEmployee(Long employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId);
    }
}
