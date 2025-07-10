package com.daedan.festabook.place.controller;

import com.daedan.festabook.place.dto.PlaceAnnouncementResponses;
import com.daedan.festabook.place.dto.PlaceImageResponses;
import com.daedan.festabook.place.dto.PlaceResponses;
import com.daedan.festabook.place.service.PlaceAnnouncementService;
import com.daedan.festabook.place.service.PlaceImageService;
import com.daedan.festabook.place.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
@Tag(name = "플레이스", description = "플레이스 관련 API")
public class PlaceController {

    private final PlaceService placeService;
    private final PlaceAnnouncementService placeAnnouncementService;
    private final PlaceImageService placeImageService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "모든 플레이스 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public PlaceResponses findAllPlace() {
        return placeService.findAllPlace();
    }

    @GetMapping("/{placeId}/announcements")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 플레이스의 모든 공지 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public PlaceAnnouncementResponses findAllPlaceAnnouncementByPlaceId(
            @PathVariable Long placeId
    ) {
        return placeAnnouncementService.findAllPlaceAnnouncementByPlaceId(placeId);
    }

    @GetMapping("/{placeId}/images")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 플레이스의 모든 이미지 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public PlaceImageResponses findAllPlaceImageByPlaceId(
            @PathVariable Long placeId
    ) {
        return placeImageService.findAllPlaceImageByPlaceId(placeId);
    }
}
