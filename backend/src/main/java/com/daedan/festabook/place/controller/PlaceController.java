package com.daedan.festabook.place.controller;

import com.daedan.festabook.global.argumentresolver.OrganizationId;
import com.daedan.festabook.place.dto.PlacePreviewResponses;
import com.daedan.festabook.place.dto.PlaceRequest;
import com.daedan.festabook.place.dto.PlaceResponse;
import com.daedan.festabook.place.dto.PlaceResponses;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "특정 조직에 대한 플레이스 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public PlaceResponse createPlace(
            @Parameter(hidden = true) @OrganizationId Long organizationId,
            @RequestBody PlaceRequest request
    ) {
        return placeService.createPlace(organizationId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 조직에 대한 플레이스 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public PlaceResponses getAllPlaces(
            @Parameter(hidden = true) @OrganizationId Long organizationId
    ) {
        return placeService.getAllPlaceByOrganizationId(organizationId);
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
    @Operation(summary = "특정 플레이스의 세부 정보와 함께 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public PlaceResponse getPlaceWithDetailByPlaceId(
            @PathVariable Long placeId
    ) {
        return placeService.getPlaceWithDetailByPlaceId(placeId);
    }
}
