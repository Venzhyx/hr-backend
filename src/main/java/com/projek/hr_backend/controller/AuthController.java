package com.projek.hr_backend.controller;

import com.projek.hr_backend.dto.ApiResponse;
import com.projek.hr_backend.dto.AuthRequest;
import com.projek.hr_backend.dto.AuthResponse;
import com.projek.hr_backend.dto.PasswordResetResponse;
import com.projek.hr_backend.dto.RegisterRequest;
import com.projek.hr_backend.model.User;
import com.projek.hr_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(
            ApiResponse.success("Login berhasil", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Registrasi berhasil", response));
    }

    /**
     * Reset password milik sendiri — userId diambil dari JWT, bukan dari body.
     * User A tidak bisa reset password user B.
     * Password baru dikembalikan sekali dalam plaintext — tidak disimpan di DB.
     */
    @PostMapping("/reset-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PasswordResetResponse>> resetOwnPassword(
            @AuthenticationPrincipal User principal) {
        PasswordResetResponse response = authService.resetOwnPassword(principal.getId());
        return ResponseEntity.ok(
            ApiResponse.success("Password berhasil direset", response));
    }
}
