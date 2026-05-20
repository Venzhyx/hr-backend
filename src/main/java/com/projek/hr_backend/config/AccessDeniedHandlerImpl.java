package com.projek.hr_backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projek.hr_backend.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Dipanggil Spring Security ketika user sudah login tapi tidak punya role yang cukup.
 * Mengembalikan JSON 403 yang konsisten dengan format ApiResponse.
 */
@Component
@RequiredArgsConstructor
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Object> body = ApiResponse.error(
            "Forbidden: Anda tidak memiliki izin untuk mengakses resource ini.");

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
