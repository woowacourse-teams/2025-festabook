package com.daedan.festabook.schedule.service;

import com.daedan.festabook.schedule.domain.Event;
import com.daedan.festabook.schedule.domain.EventDate;
import com.daedan.festabook.schedule.dto.EventDateResponses;
import com.daedan.festabook.schedule.dto.EventResponses;
import com.daedan.festabook.schedule.infrastructure.EventDateJpaRepository;
import com.daedan.festabook.schedule.infrastructure.EventJpaRepository;
import java.time.Clock;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final EventDateJpaRepository eventDateJpaRepository;
    private final EventJpaRepository eventJpaRepository;
    private final Clock clock;

    public EventDateResponses getAllEventDateByOrganizationId(Long organizationId) {
        List<EventDate> eventDates = eventDateJpaRepository.findAllByOrganizationId(organizationId).stream()
                .sorted()
                .toList();
        return EventDateResponses.from(eventDates);
    }

    public EventResponses getAllEventByEventDateId(Long eventDateId) {
        List<Event> events = eventJpaRepository.findAllByEventDateId(eventDateId).stream()
                .sorted()
                .toList();
        return EventResponses.from(events, clock);
    }
}
