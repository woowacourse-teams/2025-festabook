package com.daedan.festabook.place.controller;

import com.daedan.festabook.global.argumentresolver.FestivalId;
import com.daedan.festabook.global.security.council.CouncilDetails;
import com.daedan.festabook.place.dto.EtcPlaceUpdateRequest;
import com.daedan.festabook.place.dto.EtcPlaceUpdateResponse;
import com.daedan.festabook.place.dto.MainPlaceUpdateRequest;
import com.daedan.festabook.place.dto.MainPlaceUpdateResponse;
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
@RequestMapping("/places")
@Tag(name = "플레이스", description = "플레이스 관련 API")
public class PlaceController {

    private final PlaceService placeService;
    private final PlacePreviewService placePreviewService;

    @PreAuthorize("hasAnyRole('COUNCIL', 'ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "특정 축제에 대한 플레이스 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public PlaceResponse createPlace(
            @AuthenticationPrincipal CouncilDetails councilDetails,
            @RequestBody PlaceRequest request
    ) {
        return placeService.createPlace(councilDetails.getFestivalId(), request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 축제에 대한 플레이스 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public PlaceResponses getAllPlaceByFestivalId(
            @Parameter(hidden = true) @FestivalId Long festivalId
    ) {
        return placeService.getAllPlaceByFestivalId(festivalId);
    }

    @GetMapping("/previews")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 축제의 랜덤 정렬된 모든 플레이스 프리뷰 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public PlacePreviewResponses getAllPreviewPlaceByFestivalId(
            @Parameter(hidden = true) @FestivalId Long festivalId
    ) {
        return placePreviewService.getAllPreviewPlaceByFestivalIdSortByRandom(festivalId);
    }

    @GetMapping("/{placeId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 플레이스의 세부 정보와 함께 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public PlaceResponse getPlaceByPlaceId(
            @PathVariable Long placeId
    ) {
        return placeService.getPlaceByPlaceId(placeId);
    }

    @PreAuthorize("hasAnyRole('COUNCIL', 'ADMIN')")
    @PatchMapping("/main/{placeId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 축제에 대한 메인 플레이스 세부사항 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public MainPlaceUpdateResponse updateMainPlace(
            @PathVariable Long placeId,
            @AuthenticationPrincipal CouncilDetails councilDetails,
            @RequestBody MainPlaceUpdateRequest request
    ) {
        return placeService.updateMainPlace(councilDetails.getFestivalId(), placeId, request);
    }

    @PreAuthorize("hasAnyRole('COUNCIL', 'ADMIN')")
    @PatchMapping("/etc/{placeId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 축제에 대한 기타 플레이스 세부사항 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public EtcPlaceUpdateResponse updateEtcPlace(
            @PathVariable Long placeId,
            @AuthenticationPrincipal CouncilDetails councilDetails,
            @RequestBody EtcPlaceUpdateRequest request
    ) {
        return placeService.updateEtcPlace(councilDetails.getFestivalId(), placeId, request);
    }

    @PreAuthorize("hasAnyRole('COUNCIL', 'ADMIN')")
    @DeleteMapping("/{placeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "특정 플레이스 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    public void deleteByPlaceId(
            @PathVariable Long placeId,
            @AuthenticationPrincipal CouncilDetails councilDetails
    ) {
        placeService.deleteByPlaceId(councilDetails.getFestivalId(), placeId);
    }
}
