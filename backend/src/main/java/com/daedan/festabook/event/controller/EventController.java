package com.daedan.festabook.event.controller;

import com.daedan.festabook.event.dto.EventRequest;
import com.daedan.festabook.event.dto.EventResponses;
import com.daedan.festabook.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/event-dates")
@Tag(name = "일정", description = "일정 관련 API")
public class EventController {

    private final EventService eventService;

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "일정 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true)
    })
    public void createEvent(
            @RequestBody EventRequest request
    ) {
        eventService.createEvent(request);
    }

    @PatchMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "일정 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    })
    public void updateEvent(
            @PathVariable Long eventId,
            @RequestBody EventRequest request
    ) {
        eventService.updateEvent(eventId, request);
    }

    @DeleteMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "일정 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true)
    })
    public void deleteEvent(
            @PathVariable Long eventId
    ) {
        eventService.deleteEventByEventId(eventId);
    }

    @GetMapping("{eventDateId}/events")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 일정 날짜의 모든 일정 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    })
    public EventResponses getAllEventByEventDateId(
            @PathVariable Long eventDateId
    ) {
        return eventService.getAllEventByEventDateId(eventDateId);
    }
}
