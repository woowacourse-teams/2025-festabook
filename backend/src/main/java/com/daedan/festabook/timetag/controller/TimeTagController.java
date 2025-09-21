package com.daedan.festabook.timetag.controller;

import com.daedan.festabook.global.argumentresolver.FestivalId;
import com.daedan.festabook.global.security.council.CouncilDetails;
import com.daedan.festabook.timetag.dto.TimeTagCreateRequest;
import com.daedan.festabook.timetag.dto.TimeTagCreateResponse;
import com.daedan.festabook.timetag.dto.TimeTagResponses;
import com.daedan.festabook.timetag.dto.TimeTagUpdateRequest;
import com.daedan.festabook.timetag.dto.TimeTagUpdateResponse;
import com.daedan.festabook.timetag.service.TimeTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/time-tags")
@Tag(name = "시간 태그", description = "시간 태그 관련 API")
public class TimeTagController {

    private final TimeTagService timeTagService;

    @PreAuthorize("hasAnyRole('COUNCIL', 'ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "특정 축제에 대한 시간 태그 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public TimeTagCreateResponse createTimeTag(
            @AuthenticationPrincipal CouncilDetails councilDetails,
            @RequestBody TimeTagCreateRequest request
    ) {
        return timeTagService.createTimeTag(councilDetails.getFestivalId(), request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 축제에 대한 시간 태그 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public TimeTagResponses getAllTimeTagByFestivalId(
            @Parameter(hidden = true) @FestivalId Long festivalId
    ) {
        return timeTagService.getAllTimeTagsByFestivalId(festivalId);
    }

    @PreAuthorize("hasAnyRole('COUNCIL', 'ADMIN')")
    @PatchMapping("/{timeTagId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 시간 태그 이름 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public TimeTagUpdateResponse updateMainPlace(
            @PathVariable Long timeTagId,
            @AuthenticationPrincipal CouncilDetails councilDetails,
            @RequestBody TimeTagUpdateRequest request
    ) {
        return timeTagService.updateTimeTag(councilDetails.getFestivalId(), timeTagId, request);
    }

    @PreAuthorize("hasAnyRole('COUNCIL', 'ADMIN')")
    @DeleteMapping("/{timeTagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "특정 시간 태그 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    public void deleteTimeTag(
            @PathVariable Long timeTagId,
            @AuthenticationPrincipal CouncilDetails councilDetails
    ) {
        timeTagService.deleteTimeTag(councilDetails.getFestivalId(), timeTagId);
    }
}
