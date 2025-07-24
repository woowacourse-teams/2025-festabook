package com.daedan.festabook.place.controller;

import com.daedan.festabook.global.argumentresolver.OrganizationId;
import com.daedan.festabook.place.dto.PlaceCoordinateRequest;
import com.daedan.festabook.place.dto.PlaceCoordinateResponse;
import com.daedan.festabook.place.dto.PlaceGeographyResponses;
import com.daedan.festabook.place.service.PlaceGeographyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
@Tag(name = "플레이스 지리", description = "플레이스 지리 관련 API")
public class PlaceGeographyController {

    private final PlaceGeographyService placeGeographyService;

    @GetMapping("/geographies")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 조직의 플레이스 모든 지리 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public PlaceGeographyResponses getAllPlaceGeographyByOrganizationId(
            @Parameter(hidden = true) @OrganizationId Long organizationId
    ) {
        return placeGeographyService.getAllPlaceGeographyByOrganizationId(organizationId);
    }

    @PatchMapping("/{placeId}/geographies")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 플레이스의 좌표(위도, 경도) 업데이트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public PlaceCoordinateResponse updatePlaceCoordinate(
            @PathVariable Long placeId,
            @RequestBody PlaceCoordinateRequest request
    ) {
        return placeGeographyService.updatePlaceCoordinate(placeId, request);
    }
}
