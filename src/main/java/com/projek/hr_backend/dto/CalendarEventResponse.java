package com.projek.hr_backend.dto;

import com.projek.hr_backend.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventResponse {
    private Long id;
    private LocalDate eventDate;
    private String eventName;
    private EventType eventType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
