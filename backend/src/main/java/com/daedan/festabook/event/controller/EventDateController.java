package com.daedan.festabook.event.controller;

import com.daedan.festabook.event.dto.EventDateRequest;
import com.daedan.festabook.event.dto.EventDateResponses;
import com.daedan.festabook.event.service.EventDateService;
import com.daedan.festabook.global.argumentresolver.OrganizationId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/event-dates")
@Tag(name = "행사 날짜", description = "행사 날짜 관련 API")
public class EventDateController {

    private final EventDateService eventDateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "행사 날짜 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true)
    })
    public void createEventDate(
            @Parameter(hidden = true) @OrganizationId Long organizationId,
            @RequestBody EventDateRequest request
    ) {
        eventDateService.createEventDate(organizationId, request);
    }

    @DeleteMapping("/{eventDateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "행사 날짜 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true)
    })
    public void deleteEventDate(
            @PathVariable Long eventDateId

    ) {
        eventDateService.deleteEventDateByEventDateId(eventDateId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 조직의 모든 행사 날짜 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    })
    public EventDateResponses getAllEventDateByOrganizationId(
            @Parameter(hidden = true) @OrganizationId Long organizationId
    ) {
        return eventDateService.getAllEventDateByOrganizationId(organizationId);
    }
}
