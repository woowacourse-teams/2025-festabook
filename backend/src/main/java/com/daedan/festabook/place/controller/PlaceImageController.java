package com.daedan.festabook.place.controller;

import com.daedan.festabook.global.security.council.CouncilDetails;
import com.daedan.festabook.place.dto.PlaceImageRequest;
import com.daedan.festabook.place.dto.PlaceImageResponse;
import com.daedan.festabook.place.dto.PlaceImageSequenceUpdateRequest;
import com.daedan.festabook.place.dto.PlaceImageSequenceUpdateResponses;
import com.daedan.festabook.place.service.PlaceImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@Tag(name = "플레이스 이미지", description = "플레이스 이미지 관련 API")
public class PlaceImageController {

    private final PlaceImageService placeImageService;

    @PostMapping("/{placeId}/images")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "특정 축제에 대한 플레이스의 이미지 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public PlaceImageResponse addPlaceImage(
            @PathVariable Long placeId,
            @AuthenticationPrincipal CouncilDetails councilDetails,
            @RequestBody PlaceImageRequest request
    ) {
        return placeImageService.addPlaceImage(placeId, councilDetails.getFestivalId(), request);
    }

    @PatchMapping("/images/sequences")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 축제에 대한 플레이스의 이미지들 순서 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public PlaceImageSequenceUpdateResponses updateFestivalImagesSequence(
            @AuthenticationPrincipal CouncilDetails councilDetails,
            @RequestBody List<PlaceImageSequenceUpdateRequest> requests
    ) {
        return placeImageService.updatePlaceImagesSequence(councilDetails.getFestivalId(), requests);
    }

    @DeleteMapping("/images/{placeImageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "특정 플레이스의 이미지 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    public void deletePlaceImageByPlaceImageId(
            @PathVariable Long placeImageId,
            @AuthenticationPrincipal CouncilDetails councilDetails
    ) {
        placeImageService.deletePlaceImageByPlaceImageId(placeImageId, councilDetails.getFestivalId());
    }
}
