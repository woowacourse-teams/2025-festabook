package com.daedan.festabook.announcement.controller;

import com.daedan.festabook.announcement.dto.AnnouncementGroupedResponses;
import com.daedan.festabook.announcement.dto.AnnouncementRequest;
import com.daedan.festabook.announcement.dto.AnnouncementResponse;
import com.daedan.festabook.announcement.dto.AnnouncementUpdateRequest;
import com.daedan.festabook.announcement.service.AnnouncementService;
import com.daedan.festabook.global.argumentresolver.OrganizationId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "공지 생성 (+ FCM 알림 요청)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public AnnouncementResponse createAnnouncement(
            @Parameter(hidden = true) @OrganizationId Long organizationId,
            @RequestBody AnnouncementRequest request
    ) {
        return announcementService.createAnnouncement(organizationId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "모든 공지 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public AnnouncementGroupedResponses getGroupedAnnouncementByOrganizationId(
            @Parameter(hidden = true) @OrganizationId Long organizationId
    ) {
        return announcementService.getGroupedAnnouncementByOrganizationId(organizationId);
    }

    @PatchMapping("/{announcementId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 공지 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public AnnouncementResponse updateAnnouncement(
            @PathVariable Long announcementId,
            @RequestBody AnnouncementUpdateRequest request
    ) {
        return announcementService.updateAnnouncement(announcementId, request);
    }

    @DeleteMapping("/{announcementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "특정 공지 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    public void deleteAnnouncementByAnnouncementId(
            @PathVariable Long announcementId
    ) {
        announcementService.deleteAnnouncementByAnnouncementId(announcementId);
    }
}
