package com.daedan.festabook.schedule.service;

import com.daedan.festabook.schedule.domain.EventDay;
import com.daedan.festabook.schedule.dto.EventDayResponses;
import com.daedan.festabook.schedule.dto.EventResponses;
import com.daedan.festabook.schedule.repository.EventDayJpaRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final EventDayJpaRepository eventDayJpaRepository;

    public EventDayResponses getEventDays() {
        List<EventDay> eventDays = eventDayJpaRepository.findAll();
        Collections.sort(eventDays);
        return EventDayResponses.from(eventDays);
    }

    public EventResponses getEvents(Long eventDayId) {
        EventDay eventDay = getEventDayById(eventDayId);
        return EventResponses.from(eventDay.getEvents());
    }

    private EventDay getEventDayById(Long eventDayId) {
        return eventDayJpaRepository.findById(eventDayId)
                .orElseThrow(RuntimeException::new);
    }
}
