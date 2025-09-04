package com.daedan.festabook.event.controller;

import com.daedan.festabook.event.dto.EventDateRequest;
import com.daedan.festabook.event.dto.EventDateResponse;
import com.daedan.festabook.event.dto.EventDateResponses;
import com.daedan.festabook.event.dto.EventDateUpdateRequest;
import com.daedan.festabook.event.dto.EventDateUpdateResponse;
import com.daedan.festabook.event.service.EventDateService;
import com.daedan.festabook.global.argumentresolver.FestivalId;
import com.daedan.festabook.global.security.council.CouncilDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@Tag(name = "일정 날짜", description = "일정 날짜 관련 API")
public class EventDateController {

    private final EventDateService eventDateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "일정 날짜 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true)
    })
    public EventDateResponse createEventDate(
            @AuthenticationPrincipal CouncilDetails councilDetails,
            @RequestBody EventDateRequest request
    ) {
        return eventDateService.createEventDate(councilDetails.getFestivalId(), request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 축제의 모든 일정 날짜 조회", security = @SecurityRequirement(name = "none"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    })
    public EventDateResponses getAllEventDateByFestivalId(
            @Parameter(hidden = true) @FestivalId Long festivalId
    ) {
        return eventDateService.getAllEventDateByFestivalId(festivalId);
    }

    @PatchMapping("/{eventDateId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "일정 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    })
    public EventDateUpdateResponse updateEventDate(
            @PathVariable Long eventDateId,
            @AuthenticationPrincipal CouncilDetails councilDetails,
            @RequestBody EventDateUpdateRequest request
    ) {
        return eventDateService.updateEventDate(councilDetails.getFestivalId(), eventDateId, request);
    }

    @DeleteMapping("/{eventDateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "일정 날짜 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true)
    })
    public void deleteEventDateByEventDateId(
            @PathVariable Long eventDateId,
            @AuthenticationPrincipal CouncilDetails councilDetails
    ) {
        eventDateService.deleteEventDateByEventDateId(councilDetails.getFestivalId(), eventDateId);
    }
}
