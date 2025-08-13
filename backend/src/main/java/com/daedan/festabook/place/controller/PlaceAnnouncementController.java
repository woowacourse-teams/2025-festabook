package com.daedan.festabook.place.controller;

import com.daedan.festabook.place.dto.PlaceAnnouncementUpdateRequest;
import com.daedan.festabook.place.dto.PlaceAnnouncementUpdateResponse;
import com.daedan.festabook.place.service.PlaceAnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
@Tag(name = "플레이스 공지", description = "플레이스 공지 관련 API")
public class PlaceAnnouncementController {

    private final PlaceAnnouncementService placeAnnouncementService;

    @PatchMapping("/announcements/{placeAnnouncementId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 축제에 대한 플레이스 공지사항 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public PlaceAnnouncementUpdateResponse updatePlaceAnnouncement(
            @PathVariable Long placeAnnouncementId,
            @RequestBody PlaceAnnouncementUpdateRequest request
    ) {
        return placeAnnouncementService.updatePlaceAnnouncement(placeAnnouncementId, request);
    }
}
