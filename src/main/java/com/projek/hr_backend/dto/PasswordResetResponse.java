package com.projek.hr_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetResponse {

    private boolean success;
    private String message;

    /**
     * Password baru dalam plaintext — ditampilkan sekali ke user.
     * Tidak disimpan di DB (DB hanya menyimpan hash-nya).
     */
    private String newPassword;
}
