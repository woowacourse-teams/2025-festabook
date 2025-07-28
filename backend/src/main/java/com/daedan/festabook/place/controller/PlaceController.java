package com.daedan.festabook.place.controller;

import com.daedan.festabook.global.argumentresolver.OrganizationId;
import com.daedan.festabook.place.dto.EtcPlaceRequest;
import com.daedan.festabook.place.dto.MainPlaceRequest;
import com.daedan.festabook.place.dto.PlacePreviewResponses;
import com.daedan.festabook.place.dto.PlaceResponse;
import com.daedan.festabook.place.service.PlacePreviewService;
import com.daedan.festabook.place.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
@Tag(name = "플레이스", description = "플레이스 관련 API")
public class PlaceController {

    private final PlaceService placeService;
    private final PlacePreviewService placePreviewService;

    @PostMapping("/etc")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "etc 플레이스 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public PlaceResponse createEtcPlace(
            @Parameter(hidden = true) @OrganizationId Long organizationId,
            @RequestBody EtcPlaceRequest request
    ) {
        return placeService.createEtcPlace(organizationId, request);
    }

    @PostMapping("/main")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "main 플레이스 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public PlaceResponse createMainPlace(
            @Parameter(hidden = true) @OrganizationId Long organizationId,
            @RequestBody MainPlaceRequest request
    ) {
        return placeService.createMainPlace(organizationId, request);
    }

    @GetMapping("/previews")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 조직의 모든 플레이스 프리뷰 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public PlacePreviewResponses getAllPreviewPlaceByOrganizationId(
            @Parameter(hidden = true) @OrganizationId Long organizationId
    ) {
        return placePreviewService.getAllPreviewPlaceByOrganizationId(organizationId);
    }

    @GetMapping("/{placeId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 플레이스의 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public PlaceResponse getPlaceByPlaceId(
            @PathVariable Long placeId
    ) {
        return placeService.getPlaceByPlaceId(placeId);
    }
}
