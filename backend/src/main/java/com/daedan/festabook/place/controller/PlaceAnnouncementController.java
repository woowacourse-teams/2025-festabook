package com.daedan.festabook.place.controller;

import com.daedan.festabook.place.dto.PlaceAnnouncementRequest;
import com.daedan.festabook.place.dto.PlaceAnnouncementResponse;
import com.daedan.festabook.place.service.PlaceAnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
@Tag(name = "플레이스 공지", description = "플레이스 공지 관련 API")
public class PlaceAnnouncementController {

    private final PlaceAnnouncementService placeAnnouncementService;

    @PostMapping("/{placeId}/announcements")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "특정 축제에 대한 플레이스 공지 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public PlaceAnnouncementResponse createPlaceAnnouncement(
            @PathVariable("placeId") Long placeId,
            @RequestBody PlaceAnnouncementRequest request
    ) {
        return placeAnnouncementService.createPlaceAnnouncement(placeId, request);
    }

    @DeleteMapping("/announcements/{placeAnnouncementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "특정 플레이스의 공지 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    public void deleteByPlaceAnnouncementId(
            @PathVariable Long placeAnnouncementId
    ) {
        placeAnnouncementService.deleteByPlaceAnnouncementId(placeAnnouncementId);
    }
}
