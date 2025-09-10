package com.daedan.festabook.announcement.controller;

import com.daedan.festabook.announcement.dto.AnnouncementGroupedResponses;
import com.daedan.festabook.announcement.dto.AnnouncementPinUpdateRequest;
import com.daedan.festabook.announcement.dto.AnnouncementPinUpdateResponse;
import com.daedan.festabook.announcement.dto.AnnouncementRequest;
import com.daedan.festabook.announcement.dto.AnnouncementResponse;
import com.daedan.festabook.announcement.dto.AnnouncementUpdateRequest;
import com.daedan.festabook.announcement.dto.AnnouncementUpdateResponse;
import com.daedan.festabook.announcement.service.AnnouncementService;
import com.daedan.festabook.global.argumentresolver.FestivalId;
import com.daedan.festabook.global.security.council.CouncilDetails;
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
@RequestMapping("/announcements")
@Tag(name = "공지", description = "공지 관련 API")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PreAuthorize("hasRole('COUNCIL')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "공지 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public AnnouncementResponse createAnnouncement(
            @AuthenticationPrincipal CouncilDetails councilDetails,
            @RequestBody AnnouncementRequest request
    ) {
        return announcementService.createAnnouncement(councilDetails.getFestivalId(), request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "모든 공지 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public AnnouncementGroupedResponses getGroupedAnnouncementByFestivalId(
            @Parameter(hidden = true) @FestivalId Long festivalId
    ) {
        return announcementService.getGroupedAnnouncementByFestivalId(festivalId);
    }

    @PreAuthorize("hasRole('COUNCIL')")
    @PatchMapping("/{announcementId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 공지 내용 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public AnnouncementUpdateResponse updateAnnouncement(
            @PathVariable Long announcementId,
            @AuthenticationPrincipal CouncilDetails councilDetails,
            @RequestBody AnnouncementUpdateRequest request
    ) {
        return announcementService.updateAnnouncement(councilDetails.getFestivalId(), announcementId, request);
    }

    @PreAuthorize("hasRole('COUNCIL')")
    @PatchMapping("/{announcementId}/pin")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 공지 고정 형태 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public AnnouncementPinUpdateResponse updateAnnouncementPin(
            @PathVariable Long announcementId,
            @AuthenticationPrincipal CouncilDetails councilDetails,
            @RequestBody AnnouncementPinUpdateRequest request
    ) {
        return announcementService.updateAnnouncementPin(councilDetails.getFestivalId(), announcementId, request);
    }

    @PreAuthorize("hasRole('COUNCIL')")
    @DeleteMapping("/{announcementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "특정 공지 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    public void deleteAnnouncementByAnnouncementId(
            @PathVariable Long announcementId,
            @AuthenticationPrincipal CouncilDetails councilDetails
    ) {
        announcementService.deleteAnnouncementByAnnouncementId(councilDetails.getFestivalId(), announcementId);
    }

    @PreAuthorize("hasRole('COUNCIL')")
    @PostMapping("/{announcementId}/notifications")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "공지 FCM 알림 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public void sendAnnouncementNotification(
            @PathVariable Long announcementId,
            @AuthenticationPrincipal CouncilDetails councilDetails
    ) {
        announcementService.sendAnnouncementNotification(announcementId, councilDetails.getFestivalId());
    }
}
