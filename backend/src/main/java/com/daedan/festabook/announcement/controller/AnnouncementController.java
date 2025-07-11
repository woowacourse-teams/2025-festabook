package com.daedan.festabook.announcement.controller;

import com.daedan.festabook.announcement.dto.AnnouncementResponses;
import com.daedan.festabook.announcement.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/announcements")
@Tag(name = "공지", description = "공지 관련 API")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "모든 공지 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public AnnouncementResponses getAllAnnouncement() {
        return announcementService.getAllAnnouncement();
    }
}
