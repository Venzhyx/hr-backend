package com.projek.hr_backend.model;

/**
 * Mode operasi absensi:
 * - ONLINE  → karyawan absen mandiri via aplikasi (check-in/out aktif, scheduler skip)
 * - OFFLINE → absensi dikelola mesin/Excel (check-in/out diblokir, scheduler aktif)
 */
public enum AttendanceMode {
    ONLINE,
    OFFLINE
}
