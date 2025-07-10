package com.daedan.festabook.schedule.controller;

import com.daedan.festabook.schedule.dto.EventDayResponses;
import com.daedan.festabook.schedule.dto.EventResponses;
import com.daedan.festabook.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public EventDayResponses getEventDays() {
        return scheduleService.getEventDays();
    }

    @GetMapping("/{eventDayId}")
    public EventResponses getEvents(
            @PathVariable Long eventDayId
    ) {
        return scheduleService.getEventsByEventDayId(eventDayId);
    }
}
