package com.daedan.festabook.schedule.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.schedule.domain.Event;
import com.daedan.festabook.schedule.domain.EventDate;
import com.daedan.festabook.schedule.dto.EventDateRequest;
import com.daedan.festabook.schedule.dto.EventDateResponse;
import com.daedan.festabook.schedule.dto.EventDateResponses;
import com.daedan.festabook.schedule.dto.EventRequest;
import com.daedan.festabook.schedule.dto.EventResponse;
import com.daedan.festabook.schedule.dto.EventResponses;
import com.daedan.festabook.schedule.infrastructure.EventDateJpaRepository;
import com.daedan.festabook.schedule.infrastructure.EventJpaRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final EventDateJpaRepository eventDateJpaRepository;
    private final EventJpaRepository eventJpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;
    private final Clock clock;

    public EventDateResponse createEventDate(Long organizationId, EventDateRequest request) {
        // TODO: 추후 검증 추가 관리자 조직 권한과 eventDate의 조직 id 비교하기
        validateDuplicatedEventDate(organizationId, request.date());

        Organization organization = getOrganizationById(organizationId);
        EventDate eventDate = request.toEntity(organization);
        EventDate savedEventDate = eventDateJpaRepository.save(eventDate);

        return EventDateResponse.from(savedEventDate);
    }

    private void validateDuplicatedEventDate(Long organizationId, LocalDate date) {
        if (eventDateJpaRepository.existsByOrganizationIdAndDate(organizationId, date)) {
            throw new BusinessException("이미 존재하는 일정 날짜입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public void deleteEventDate(Long eventDateId) {
        // TODO: 추후 검증 추가 관리자 조직 권한과 eventDate의 조직 id 비교하기
        eventJpaRepository.deleteAllByEventDateId(eventDateId);
        eventDateJpaRepository.deleteById(eventDateId);
    }

    public EventDateResponses getAllEventDateByOrganizationId(Long organizationId) {
        List<EventDate> eventDates = eventDateJpaRepository.findAllByOrganizationId(organizationId).stream()
                .sorted()
                .toList();
        return EventDateResponses.from(eventDates);
    }

    public EventResponse createEvent(EventRequest request) {
        // TODO: 추후 검증 추가 관리자 조직 권한과 eventDate의 조직 id 비교하기
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

    public void deleteEvent(Long eventId) {
        eventJpaRepository.deleteById(eventId);
    }

    @Transactional(readOnly = true)
    public EventResponses getAllEventByEventDateId(Long eventDateId) {
        List<Event> events = eventJpaRepository.findAllByEventDateId(eventDateId).stream()
                .sorted()
                .toList();
        return EventResponses.from(events, clock);
    }

    private Organization getOrganizationById(Long organizationId) {
        return organizationJpaRepository.findById(organizationId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 조직입니다.", HttpStatus.BAD_REQUEST));
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
