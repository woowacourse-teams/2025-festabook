package com.daedan.festabook.lineup.controller;

import com.daedan.festabook.global.argumentresolver.FestivalId;
import com.daedan.festabook.lineup.dto.LineupRequest;
import com.daedan.festabook.lineup.dto.LineupResponse;
import com.daedan.festabook.lineup.dto.LineupResponses;
import com.daedan.festabook.lineup.dto.LineupUpdateRequest;
import com.daedan.festabook.lineup.service.LineupService;
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
@RequestMapping("/lineups")
@Tag(name = "축제 라인업", description = "축제 라인업 관련 API")
public class LineupController {

    private final LineupService lineupService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "특정 축제의 라인업 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public LineupResponse addLineup(
            @Parameter(hidden = true) @FestivalId Long festivalId,
            @RequestBody LineupRequest request
    ) {
        return lineupService.addLineup(festivalId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 축제의 라인업 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public LineupResponses getAllLineupByFestivalId(
            @Parameter(hidden = true) @FestivalId Long festivalId
    ) {
        return lineupService.getAllLineupByFestivalId(festivalId);
    }

    @PatchMapping("/{lineupId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 축제의 라인업 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public LineupResponse updateLineup(
            @Parameter(hidden = true) @FestivalId Long festivalId,
            @PathVariable Long lineupId,
            @RequestBody LineupUpdateRequest request
    ) {
        return lineupService.updateLineup(festivalId, lineupId, request);
    }

    @DeleteMapping("/{lineupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "특정 축제의 라인업 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    public void deleteLineupByLineupId(
            @PathVariable Long lineupId
    ) {
        lineupService.deleteLineupByLineupId(lineupId);
    }
}
