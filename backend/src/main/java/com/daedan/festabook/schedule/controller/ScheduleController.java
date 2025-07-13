package com.daedan.festabook.schedule.controller;

import com.daedan.festabook.schedule.dto.EventDateResponses;
import com.daedan.festabook.schedule.dto.EventResponses;
import com.daedan.festabook.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "일정", description = "일정 관련 API")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "모든 일정 날짜 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public EventDateResponses getAllEventDate() {
        return scheduleService.getAllEventDate();
    }

    @GetMapping("/{eventDateId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 일정의 모든 이벤트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public EventResponses getAllEventByEventDateId(
            @PathVariable Long eventDateId
    ) {
        return scheduleService.getAllEventByEventDateId(eventDateId);
    }
}
