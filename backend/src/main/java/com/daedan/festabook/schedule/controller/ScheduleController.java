package com.daedan.festabook.schedule.controller;

import com.daedan.festabook.global.argumentresolver.OrganizationId;
import com.daedan.festabook.schedule.dto.EventDateRequest;
import com.daedan.festabook.schedule.dto.EventDateResponse;
import com.daedan.festabook.schedule.dto.EventDateResponses;
import com.daedan.festabook.schedule.dto.EventRequest;
import com.daedan.festabook.schedule.dto.EventResponse;
import com.daedan.festabook.schedule.dto.EventResponses;
import com.daedan.festabook.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
@Tag(name = "일정", description = "축제 날짜, 이벤트 관련 API")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "축제 날짜 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true)
    })
    public EventDateResponse createEventDate(
            @Parameter(hidden = true) @OrganizationId Long organizationId,
            @RequestBody EventDateRequest request
    ) {
        return scheduleService.createEventDate(organizationId, request);
    }

    @DeleteMapping("/{eventDateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "축제 날짜 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true)
    })
    public void deleteEventDate(
            @PathVariable Long eventDateId
    ) {
        scheduleService.deleteEventDate(eventDateId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "축제의 모든 축제 날짜 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    })
    public EventDateResponses getAllEventDateByOrganizationId(
            @Parameter(hidden = true) @OrganizationId Long organizationId
    ) {
        return scheduleService.getAllEventDateByOrganizationId(organizationId);
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "이벤트 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true)
    })
    public EventResponse createEvent(
            @RequestBody EventRequest request
    ) {
        return scheduleService.createEvent(request);
    }

    @PatchMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "이벤트 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    })
    public EventResponse updateEvent(
            @PathVariable Long eventId,
            @RequestBody EventRequest request
    ) {
        return scheduleService.updateEvent(eventId, request);
    }

    @DeleteMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "이벤트 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true)
    })
    public void deleteEvent(
            @PathVariable Long eventId
    ) {
        scheduleService.deleteEvent(eventId);
    }

    @GetMapping("/{eventDateId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "축제 날짜의 모든 이벤트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    })
    public EventResponses getAllEventByEventDateId(
            @PathVariable Long eventDateId
    ) {
        return scheduleService.getAllEventByEventDateId(eventDateId);
    }
}
