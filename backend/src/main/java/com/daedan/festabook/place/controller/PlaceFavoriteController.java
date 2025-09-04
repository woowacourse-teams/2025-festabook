package com.daedan.festabook.place.controller;

import com.daedan.festabook.place.dto.PlaceFavoriteRequest;
import com.daedan.festabook.place.dto.PlaceFavoriteResponse;
import com.daedan.festabook.place.service.PlaceFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
@Tag(name = "플레이스 즐겨찾기", description = "플레이스 즐겨찾기 관련 API")
public class PlaceFavoriteController {

    private final PlaceFavoriteService placeFavoriteService;

    @PostMapping("/{placeId}/favorites")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "특정 플레이스의 즐겨찾기 추가", security = @SecurityRequirement(name = "none"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public PlaceFavoriteResponse createPlaceFavorite(
            @PathVariable Long placeId,
            @RequestBody PlaceFavoriteRequest request
    ) {
        return placeFavoriteService.addPlaceFavorite(placeId, request);
    }

    @DeleteMapping("/favorites/{placeFavoriteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "특정 플레이스의 즐겨찾기 취소", security = @SecurityRequirement(name = "none"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    public void deletePlaceFavorite(
            @PathVariable Long placeFavoriteId
    ) {
        placeFavoriteService.removePlaceFavorite(placeFavoriteId);
    }
}
