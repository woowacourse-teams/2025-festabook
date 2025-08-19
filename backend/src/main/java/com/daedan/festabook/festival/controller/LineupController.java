package com.daedan.festabook.festival.controller;

import com.daedan.festabook.festival.dto.LineupRequest;
import com.daedan.festabook.festival.dto.LineupResponse;
import com.daedan.festabook.festival.service.LineupService;
import com.daedan.festabook.global.argumentresolver.FestivalId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/festivals/lineups")
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
}
