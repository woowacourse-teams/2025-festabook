package com.daedan.festabook.place.controller;

import com.daedan.festabook.place.dto.PlaceBookmarkRequest;
import com.daedan.festabook.place.dto.PlaceBookmarkResponse;
import com.daedan.festabook.place.service.PlaceBookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "플레이스 북마크", description = "플레이스 북마크 관련 API")
public class PlaceBookmarkController {

    private final PlaceBookmarkService placeBookmarkService;

    @PostMapping("/{placeId}/bookmarks")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "특정 플레이스의 북마크 생성 (+ FCM 토픽 구독)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public PlaceBookmarkResponse createPlaceBookmark(
            @PathVariable Long placeId,
            @RequestBody PlaceBookmarkRequest request
    ) {
        return placeBookmarkService.createPlaceBookmark(placeId, request);
    }

    @DeleteMapping("/bookmarks/{placeBookmarkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "특정 플레이스의 북마크 삭제 (+ FCM 토픽 구독 취소)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    public void deletePlaceBookmark(
            @PathVariable Long placeBookmarkId
    ) {
        placeBookmarkService.deletePlaceBookmark(placeBookmarkId);
    }
}
