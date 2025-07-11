package com.daedan.festabook.schedule.service;

import com.daedan.festabook.schedule.domain.EventDay;
import com.daedan.festabook.schedule.dto.EventDayResponses;
import com.daedan.festabook.schedule.dto.EventResponses;
import com.daedan.festabook.schedule.repository.EventDayJpaRepository;
import com.daedan.festabook.schedule.repository.EventJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final EventDayJpaRepository eventDayJpaRepository;
    private final EventJpaRepository eventJpaRepository;

    public EventDayResponses getAllEventDay() {
        List<EventDay> eventDays = eventDayJpaRepository.findAll().stream()
                .sorted()
                .toList();
        return EventDayResponses.from(eventDays);
    }

    public EventResponses getAllEventByEventDayId(Long eventDayId) {
        return EventResponses.from(eventJpaRepository.findAllByEventDayId(eventDayId));
    }
}
