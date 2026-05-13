package com.projek.hr_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AttendanceScheduler {

    private final AttendanceService attendanceService;

    private static final String SCAN_FOLDER      = "/app/attendance";
    private static final String PROCESSED_FOLDER = "/app/attendance/processed";

    @Scheduled(fixedRate = 60000)
    public void processAttendanceFiles() {
        File scanDir = new File(SCAN_FOLDER);

        if (!scanDir.exists() || !scanDir.isDirectory()) {
            System.out.println("[Scheduler] Folder tidak ditemukan: " + SCAN_FOLDER);
            return;
        }

        File[] files = scanDir.listFiles((dir, name) -> name.endsWith(".xlsx"));

        if (files == null || files.length == 0) {
            System.out.println("[Scheduler] Tidak ada file .xlsx di folder: " + SCAN_FOLDER);
            return;
        }

        File processedDir = new File(PROCESSED_FOLDER);
        if (!processedDir.exists()) {
            processedDir.mkdirs();
        }

        for (File file : files) {
            System.out.println("[Scheduler] Memproses file: " + file.getName());
            try {
                attendanceService.importExcel(file);

                File newFile = new File(processedDir, file.getName());
                boolean moved = file.renameTo(newFile);

                if (moved) {
                    System.out.println("[Scheduler] File berhasil dipindahkan: " + file.getName());
                } else {
                    System.out.println("[Scheduler] Gagal memindahkan file: " + file.getName());
                }
            } catch (Exception e) {
                System.err.println("[Scheduler] Gagal memproses file: " + file.getName() + " - " + e.getMessage());
            }
        }
    }
}
