package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.AuthRequest;
import com.projek.hr_backend.dto.AuthResponse;
import com.projek.hr_backend.dto.PasswordResetResponse;
import com.projek.hr_backend.dto.RegisterRequest;
import com.projek.hr_backend.exception.BadRequestException;
import com.projek.hr_backend.exception.ResourceNotFoundException;
import com.projek.hr_backend.model.Role;
import com.projek.hr_backend.model.User;
import com.projek.hr_backend.repository.EmployeeRepository;
import com.projek.hr_backend.repository.UserRepository;
import com.projek.hr_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository        userRepository;
    private final EmployeeRepository    employeeRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtUtil               jwtUtil;
    private final AuthenticationManager authenticationManager;

    // ─── Register ─────────────────────────────────────────────────────────────

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Cek username sudah dipakai
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException(
                "Username '" + request.getUsername() + "' sudah digunakan");
        }

        // Kalau role EMPLOYEE, employeeId wajib ada dan valid
        if (request.getRole() == Role.EMPLOYEE) {
            if (request.getEmployeeId() == null) {
                throw new BadRequestException(
                    "employeeId wajib diisi untuk role EMPLOYEE");
            }
            if (!employeeRepository.existsById(request.getEmployeeId())) {
                throw new BadRequestException(
                    "Employee tidak ditemukan dengan id: " + request.getEmployeeId());
            }
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .employeeId(request.getEmployeeId())
                .isActive(true)
                .build();

        User saved = userRepository.save(user);
        log.info("User registered: {} (role={})", saved.getUsername(), saved.getRole());

        String token = jwtUtil.generateToken(saved);
        return buildResponse(saved, token);
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    public AuthResponse login(AuthRequest request) {
        // Spring Security akan lempar exception jika credentials salah
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("User tidak ditemukan"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new BadRequestException("Akun tidak aktif. Hubungi administrator.");
        }

        String token = jwtUtil.generateToken(user);
        log.info("User logged in: {} (role={})", user.getUsername(), user.getRole());
        return buildResponse(user, token);
    }

    // ─── Reset Own Password ───────────────────────────────────────────────────

    /**
     * Generate password baru secara acak untuk user yang sedang login.
     * userId diambil dari JWT (via @AuthenticationPrincipal di controller),
     * bukan dari request body — user A tidak bisa reset password user B.
     *
     * Password baru dikembalikan sekali dalam plaintext ke FE.
     * DB hanya menyimpan hash-nya.
     */
    @Transactional
    public PasswordResetResponse resetOwnPassword(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "User tidak ditemukan dengan id: " + userId));

        // Generate 6 karakter acak — huruf besar, huruf kecil, angka
        String newPassword = generateSecurePassword(6);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password reset for user: {} (id={})", user.getUsername(), userId);

        return PasswordResetResponse.builder()
                .success(true)
                .message("Password berhasil direset. Simpan password baru ini — tidak akan ditampilkan lagi.")
                .newPassword(newPassword)
                .build();
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private static final String PASSWORD_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private String generateSecurePassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(PASSWORD_CHARS.charAt(
                SECURE_RANDOM.nextInt(PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }

    private AuthResponse buildResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .employeeId(user.getEmployeeId())
                .build();
    }
}
