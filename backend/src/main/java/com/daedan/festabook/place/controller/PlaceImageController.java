package com.daedan.festabook.place.controller;

import com.daedan.festabook.place.service.PlaceImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
@Tag(name = "플레이스", description = "플레이스 관련 API")
public class PlaceImageController {

    private final PlaceImageService placeImageService;

    @DeleteMapping("/images/{placeImageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "특정 플레이스의 이미지 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    public void deletePlaceImageByPlaceImageId(
            @PathVariable Long placeImageId
    ) {
        placeImageService.deletePlaceImageByPlaceImageId(placeImageId);
    }
}
