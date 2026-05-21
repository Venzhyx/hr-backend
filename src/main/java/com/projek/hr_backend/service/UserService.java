package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.UpdateUserRequest;
import com.projek.hr_backend.dto.UserResponse;
import com.projek.hr_backend.exception.BadRequestException;
import com.projek.hr_backend.exception.ResourceNotFoundException;
import com.projek.hr_backend.model.Role;
import com.projek.hr_backend.model.User;
import com.projek.hr_backend.repository.EmployeeRepository;
import com.projek.hr_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository     userRepository;
    private final EmployeeRepository employeeRepository;

    // ─── Get All ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── Get By Id ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = findOrThrow(id);

        if (request.getRole() != null) {
            // Kalau diubah ke EMPLOYEE, employeeId wajib ada
            if (request.getRole() == Role.EMPLOYEE) {
                Long empId = request.getEmployeeId() != null
                        ? request.getEmployeeId()
                        : user.getEmployeeId();
                if (empId == null) {
                    throw new BadRequestException(
                        "employeeId wajib diisi saat role diubah ke EMPLOYEE");
                }
                if (!employeeRepository.existsById(empId)) {
                    throw new BadRequestException(
                        "Employee tidak ditemukan dengan id: " + empId);
                }
            }
            user.setRole(request.getRole());
        }

        if (request.getEmployeeId() != null) {
            if (!employeeRepository.existsById(request.getEmployeeId())) {
                throw new BadRequestException(
                    "Employee tidak ditemukan dengan id: " + request.getEmployeeId());
            }
            user.setEmployeeId(request.getEmployeeId());
        }

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        return toResponse(userRepository.save(user));
    }

    // ─── Toggle Active ────────────────────────────────────────────────────────

    @Transactional
    public UserResponse toggleActive(Long id) {
        User user = findOrThrow(id);
        user.setIsActive(!Boolean.TRUE.equals(user.getIsActive()));
        return toResponse(userRepository.save(user));
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User tidak ditemukan dengan id: " + id);
        }
        userRepository.deleteById(id);
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "User tidak ditemukan dengan id: " + id));
    }

    private UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .role(u.getRole())
                .employeeId(u.getEmployeeId())
                .isActive(u.getIsActive())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }
}
