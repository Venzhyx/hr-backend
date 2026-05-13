package com.projek.hr_backend.repository;

import com.projek.hr_backend.model.CalendarEvent;
import com.projek.hr_backend.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    List<CalendarEvent> findByEventType(EventType eventType);
    List<CalendarEvent> findByEventDateBetween(LocalDate startDate, LocalDate endDate);
}
