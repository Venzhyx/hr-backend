package com.projek.hr_backend.dto;

import com.projek.hr_backend.model.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventRequest {
    
    @NotNull(message = "Event date is required")
    private LocalDate eventDate;
    
    @NotBlank(message = "Event name is required")
    private String eventName;
    
    @NotNull(message = "Event type is required")
    private EventType eventType;
}
