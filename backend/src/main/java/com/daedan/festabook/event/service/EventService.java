package com.daedan.festabook.event.service;

import com.daedan.festabook.event.domain.Event;
import com.daedan.festabook.event.domain.EventDate;
import com.daedan.festabook.event.dto.EventRequest;
import com.daedan.festabook.event.dto.EventResponse;
import com.daedan.festabook.event.dto.EventResponses;
import com.daedan.festabook.event.dto.EventUpdateRequest;
import com.daedan.festabook.event.dto.EventUpdateResponse;
import com.daedan.festabook.event.infrastructure.EventDateJpaRepository;
import com.daedan.festabook.event.infrastructure.EventJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
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

    @Transactional
    public EventResponse createEvent(Long festivalId, EventRequest request) {
        EventDate eventDate = getEventDateById(request.eventDateId());
        validateEventDateBelongsToFestival(eventDate, festivalId);
        Event event = request.toEntity(eventDate);
        Event savedEvent = eventJpaRepository.save(event);

        return EventResponse.from(savedEvent, clock);
    }

    @Transactional(readOnly = true)
    public EventResponses getAllEventByEventDateId(Long eventDateId) {
        List<Event> events = eventJpaRepository.findAllByEventDateId(eventDateId).stream()
                .sorted()
                .toList();
        return EventResponses.from(events, clock);
    }

    @Transactional
    public EventUpdateResponse updateEvent(Long festivalId, Long eventId, EventUpdateRequest request) {
        Event event = getEventById(eventId);
        validateEventBelongsToFestival(event, festivalId);
        EventDate newEventDate = getEventDateById(request.eventDateId());
        validateEventDateBelongsToFestival(newEventDate, festivalId);
        Event newEvent = request.toEntity(newEventDate);

        event.updateEvent(newEvent);
        return EventUpdateResponse.from(event, clock);
    }

    @Transactional
    public void deleteEventByEventId(Long festivalId, Long eventId) {
        Event event = getEventById(eventId);
        validateEventBelongsToFestival(event, festivalId);

        eventJpaRepository.deleteById(eventId);
    }

    private EventDate getEventDateById(Long eventDateId) {
        return eventDateJpaRepository.findById(eventDateId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 일정 날짜입니다.", HttpStatus.BAD_REQUEST));
    }

    private Event getEventById(Long eventId) {
        return eventJpaRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 일정입니다.", HttpStatus.BAD_REQUEST));
    }

    private void validateEventBelongsToFestival(Event event, Long festivalId) {
        if (!event.isFestivalIdEqualTo(festivalId)) {
            throw new BusinessException("해당 축제의 일정이 아닙니다.", HttpStatus.FORBIDDEN);
        }
    }

    private void validateEventDateBelongsToFestival(EventDate eventDate, Long festivalId) {
        if (!eventDate.isFestivalIdEqualTo(festivalId)) {
            throw new BusinessException("해당 축제의 일정 날짜가 아닙니다.", HttpStatus.FORBIDDEN);
        }
    }
}
