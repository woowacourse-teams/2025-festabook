package com.daedan.festabook.schedule.controller;

import com.daedan.festabook.schedule.dto.EventRequest;
import com.daedan.festabook.schedule.dto.EventResponse;
import com.daedan.festabook.schedule.dto.EventResponses;
import com.daedan.festabook.schedule.service.EventService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules/events")
@Tag(name = "일정 이벤트", description = "이벤트 관련 API")
public class EventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "이벤트 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true)
    })
    public EventResponse createEvent(
            @RequestBody EventRequest request
    ) {
        return eventService.createEvent(request);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "이벤트 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    })
    public EventResponse updateEvent(
            @PathVariable Long eventId,
            @RequestBody EventRequest request
    ) {
        return eventService.updateEvent(eventId, request);
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "이벤트 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true)
    })
    public void deleteEvent(
            @PathVariable Long eventId
    ) {
        eventService.deleteEvent(eventId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "축제 날짜의 모든 이벤트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    })
    public EventResponses getAllEventByEventDateId(
            @RequestParam Long eventDateId
    ) {
        return eventService.getAllEventByEventDateId(eventDateId);
    }
}
