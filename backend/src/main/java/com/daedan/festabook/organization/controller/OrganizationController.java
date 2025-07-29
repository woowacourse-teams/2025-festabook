package com.daedan.festabook.organization.controller;

import com.daedan.festabook.global.argumentresolver.OrganizationId;
import com.daedan.festabook.organization.dto.FestivalResponse;
import com.daedan.festabook.organization.dto.OrganizationGeographyResponse;
import com.daedan.festabook.organization.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/organizations")
@Tag(name = "조직", description = "조직 관련 API")
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping("/geography")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 조직의 초기 지리 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public OrganizationGeographyResponse getOrganizationGeographyByOrganizationId(
            @Parameter(hidden = true) @OrganizationId Long organizationId
    ) {
        return organizationService.getOrganizationGeographyByOrganizationId(organizationId);
    }

    @GetMapping("/festivals")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 조직의 축제 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public FestivalResponse getFestivalByOrganizationId(
            @Parameter(hidden = true) @OrganizationId Long organizationId
    ) {
        return organizationService.getFestivalByOrganizationId(organizationId);
    }
}
