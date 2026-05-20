package com.projek.hr_backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projek.hr_backend.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Dipanggil Spring Security ketika request tidak punya token / token invalid.
 * Mengembalikan JSON 401 yang konsisten dengan format ApiResponse.
 */
@Component
@RequiredArgsConstructor
public class AuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Object> body = ApiResponse.error(
            "Unauthorized: Token tidak valid atau tidak ditemukan. Silakan login.");

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
