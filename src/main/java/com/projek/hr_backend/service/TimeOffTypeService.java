package com.projek.hr_backend.service;

import com.projek.hr_backend.dto.TimeOffTypeRequest;
import com.projek.hr_backend.dto.TimeOffTypeResponse;
import com.projek.hr_backend.exception.ResourceNotFoundException;
import com.projek.hr_backend.model.TimeOffType;
import com.projek.hr_backend.repository.TimeOffTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeOffTypeService {
    
    private final TimeOffTypeRepository repository;
    
    public List<TimeOffTypeResponse> getAllTimeOffTypes() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public TimeOffTypeResponse getTimeOffTypeById(Long id) {
        TimeOffType timeOffType = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Time off type not found with id: " + id));
        return mapToResponse(timeOffType);
    }
    
    @Transactional
    public TimeOffTypeResponse createTimeOffType(TimeOffTypeRequest request) {
        TimeOffType timeOffType = new TimeOffType();
        timeOffType.setName(request.getName());
        timeOffType.setType(request.getType());
        timeOffType.setMaxDaysPerSubmission(request.getMaxDaysPerSubmission());
        timeOffType.setStatus(request.getStatus());
        
        TimeOffType saved = repository.save(timeOffType);
        return mapToResponse(saved);
    }
    
    @Transactional
    public TimeOffTypeResponse updateTimeOffType(Long id, TimeOffTypeRequest request) {
        TimeOffType timeOffType = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Time off type not found with id: " + id));
        
        timeOffType.setName(request.getName());
        timeOffType.setType(request.getType());
        timeOffType.setMaxDaysPerSubmission(request.getMaxDaysPerSubmission());
        timeOffType.setStatus(request.getStatus());
        
        TimeOffType saved = repository.save(timeOffType);
        return mapToResponse(saved);
    }
    
    @Transactional
    public void deleteTimeOffType(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Time off type not found with id: " + id);
        }
        repository.deleteById(id);
    }
    
    private TimeOffTypeResponse mapToResponse(TimeOffType timeOffType) {
        return new TimeOffTypeResponse(
            timeOffType.getId(),
            timeOffType.getName(),
            timeOffType.getType(),
            timeOffType.getMaxDaysPerSubmission(),
            timeOffType.getStatus(),
            timeOffType.getCreatedAt(),
            timeOffType.getUpdatedAt()
        );
    }
}
