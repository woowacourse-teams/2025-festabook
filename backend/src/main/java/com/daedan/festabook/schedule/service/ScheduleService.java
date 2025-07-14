package com.daedan.festabook.schedule.service;

import com.daedan.festabook.schedule.domain.EventDate;
import com.daedan.festabook.schedule.dto.EventDateResponses;
import com.daedan.festabook.schedule.dto.EventResponses;
import com.daedan.festabook.schedule.infrastructure.EventDateJpaRepository;
import com.daedan.festabook.schedule.infrastructure.EventJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final EventDateJpaRepository eventDateJpaRepository;
    private final EventJpaRepository eventJpaRepository;

    public EventDateResponses getAllEventDateByOrganizationId(Long organizationId) {
        List<EventDate> eventDates = eventDateJpaRepository.findAllByOrganizationId(organizationId).stream()
                .sorted()
                .toList();
        return EventDateResponses.from(eventDates);
    }

    public EventResponses getAllEventByEventDateId(Long eventDateId) {
        return EventResponses.from(eventJpaRepository.findAllByEventDateId(eventDateId));
    }
}
