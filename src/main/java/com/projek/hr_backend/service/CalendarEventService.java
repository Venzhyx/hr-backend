package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.CalendarEventRequest;
import com.projek.hr_backend.dto.CalendarEventResponse;
import com.projek.hr_backend.exception.ResourceNotFoundException;
import com.projek.hr_backend.model.CalendarEvent;
import com.projek.hr_backend.repository.CalendarEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarEventService {
    
    private final CalendarEventRepository repository;
    
    public List<CalendarEventResponse> getAllEvents() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public CalendarEventResponse getEventById(Long id) {
        CalendarEvent event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calendar event not found with id: " + id));
        return mapToResponse(event);
    }
    
    @Transactional
    public CalendarEventResponse createEvent(CalendarEventRequest request) {
        CalendarEvent event = new CalendarEvent();
        event.setEventDate(request.getEventDate());
        event.setEventName(request.getEventName());
        event.setEventType(request.getEventType());
        
        CalendarEvent saved = repository.save(event);
        return mapToResponse(saved);
    }
    
    @Transactional
    public CalendarEventResponse updateEvent(Long id, CalendarEventRequest request) {
        CalendarEvent event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calendar event not found with id: " + id));
        
        event.setEventDate(request.getEventDate());
        event.setEventName(request.getEventName());
        event.setEventType(request.getEventType());
        
        CalendarEvent saved = repository.save(event);
        return mapToResponse(saved);
    }
    
    @Transactional
    public void deleteEvent(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Calendar event not found with id: " + id);
        }
        repository.deleteById(id);
    }
    
    private CalendarEventResponse mapToResponse(CalendarEvent event) {
        return new CalendarEventResponse(
            event.getId(),
            event.getEventDate(),
            event.getEventName(),
            event.getEventType(),
            event.getCreatedAt(),
            event.getUpdatedAt()
        );
    }
}
