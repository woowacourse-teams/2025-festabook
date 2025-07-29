package com.daedan.festabook.schedule.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.schedule.domain.Event;
import com.daedan.festabook.schedule.domain.EventDate;
import com.daedan.festabook.schedule.dto.EventRequest;
import com.daedan.festabook.schedule.dto.EventResponse;
import com.daedan.festabook.schedule.dto.EventResponses;
import com.daedan.festabook.schedule.infrastructure.EventDateJpaRepository;
import com.daedan.festabook.schedule.infrastructure.EventJpaRepository;
import java.time.Clock;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final Clock clock;
    private final EventDateJpaRepository eventDateJpaRepository;
    private final EventJpaRepository eventJpaRepository;

    public EventResponse createEvent(EventRequest request) {
        EventDate eventDate = getEventDateById(request.eventDateId());
        Event event = request.toEntity(eventDate);
        Event savedEvent = eventJpaRepository.save(event);

        return EventResponse.from(savedEvent, clock);
    }

    @Transactional
    public EventResponse updateEvent(Long eventId, EventRequest request) {
        Event event = getEventById(eventId);

        Event newEvent = request.toEntity(event.getEventDate());
        event.updateEvent(newEvent);

        return EventResponse.from(event, clock);
    }

    public void deleteEventByEventId(Long eventId) {
        eventJpaRepository.deleteById(eventId);
    }

    @Transactional(readOnly = true)
    public EventResponses getAllEventByEventDateId(Long eventDateId) {
        List<Event> events = eventJpaRepository.findAllByEventDateId(eventDateId).stream()
                .sorted()
                .toList();
        return EventResponses.from(events, clock);
    }

    private EventDate getEventDateById(Long eventDateId) {
        return eventDateJpaRepository.findById(eventDateId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 일정 날짜입니다.", HttpStatus.BAD_REQUEST));
    }

    private Event getEventById(Long eventId) {
        return eventJpaRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 일정입니다.", HttpStatus.BAD_REQUEST));
    }
}
