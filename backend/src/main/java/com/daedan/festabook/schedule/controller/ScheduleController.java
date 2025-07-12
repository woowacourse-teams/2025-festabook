package com.daedan.festabook.schedule.controller;

import com.daedan.festabook.schedule.dto.EventDateResponses;
import com.daedan.festabook.schedule.dto.EventResponses;
import com.daedan.festabook.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public EventDateResponses getAllEventDate() {
        return scheduleService.getAllEventDate();
    }

    @GetMapping("/{eventDateId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponses getAllEventByEventDateId(
            @PathVariable Long eventDateId
    ) {
        return scheduleService.getAllEventByEventDateId(eventDateId);
    }
}
