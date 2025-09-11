package com.daedan.festabook.council.controller;

import com.daedan.festabook.council.dto.CouncilLoginRequest;
import com.daedan.festabook.council.dto.CouncilLoginResponse;
import com.daedan.festabook.council.dto.CouncilRequest;
import com.daedan.festabook.council.dto.CouncilResponse;
import com.daedan.festabook.council.dto.CouncilUpdateRequest;
import com.daedan.festabook.council.dto.CouncilUpdateResponse;
import com.daedan.festabook.council.service.CouncilService;
import com.daedan.festabook.global.security.council.CouncilDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/councils")
@Tag(name = "학생회", description = "학생회 관련 API")
public class CouncilController {

    private final CouncilService councilService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "학생회 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true)
    })
    public CouncilResponse createCouncil(
            @RequestBody CouncilRequest request
    ) {
        return councilService.createCouncil(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "학생회 로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true)
    })
    public CouncilLoginResponse loginCouncil(
            @RequestBody CouncilLoginRequest request
    ) {
        return councilService.loginCouncil(request);
    }

    @PreAuthorize("hasRole('COUNCIL')")
    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "학생회 비밀번호 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    })
    public CouncilUpdateResponse updatePassword(
            @AuthenticationPrincipal CouncilDetails councilDetails,
            @RequestBody CouncilUpdateRequest request
    ) {
        return councilService.updatePassword(councilDetails.getCouncil().getId(), request);
    }
}
