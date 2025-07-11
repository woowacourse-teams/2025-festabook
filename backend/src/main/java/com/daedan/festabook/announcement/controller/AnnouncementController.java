package com.daedan.festabook.announcement.controller;

import com.daedan.festabook.announcement.dto.AnnouncementResponses;
import com.daedan.festabook.announcement.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public AnnouncementResponses getAllAnnouncement() {
        return announcementService.getAllAnnouncement();
    }
}
