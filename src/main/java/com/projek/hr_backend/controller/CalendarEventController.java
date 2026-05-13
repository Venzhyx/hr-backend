package com.projek.hr_backend.controller;

import com.projek.hr_backend.dto.ApiResponse;
import com.projek.hr_backend.dto.CalendarEventRequest;
import com.projek.hr_backend.dto.CalendarEventResponse;
import com.projek.hr_backend.service.CalendarEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar-events")
@RequiredArgsConstructor
public class CalendarEventController {
    
    private final CalendarEventService service;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<CalendarEventResponse>>> getAllEvents() {
        List<CalendarEventResponse> responses = service.getAllEvents();
        return ResponseEntity.ok(ApiResponse.success("Calendar events retrieved successfully", responses));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CalendarEventResponse>> getEventById(@PathVariable Long id) {
        CalendarEventResponse response = service.getEventById(id);
        return ResponseEntity.ok(ApiResponse.success("Calendar event retrieved successfully", response));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<CalendarEventResponse>> createEvent(
            @Valid @RequestBody CalendarEventRequest request) {
        CalendarEventResponse response = service.createEvent(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Calendar event created successfully", response));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CalendarEventResponse>> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody CalendarEventRequest request) {
        CalendarEventResponse response = service.updateEvent(id, request);
        return ResponseEntity.ok(ApiResponse.success("Calendar event updated successfully", response));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
        service.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.success("Calendar event deleted successfully", null));
    }
}
