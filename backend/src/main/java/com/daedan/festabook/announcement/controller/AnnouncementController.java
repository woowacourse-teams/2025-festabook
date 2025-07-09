package com.daedan.festabook.announcement.controller;

import com.daedan.festabook.announcement.dto.AnnouncementRequest;
import com.daedan.festabook.announcement.dto.AnnouncementResponse;
import com.daedan.festabook.announcement.service.AnnouncementService;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/announcement")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    public ResponseEntity<AnnouncementResponse> createAnnouncement(
            @RequestBody final AnnouncementRequest request
    ) {
        final AnnouncementResponse response = announcementService.createAnnouncement(request);
        return ResponseEntity.created(URI.create("/announcement")).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AnnouncementResponse>> findAllAnnouncement() {
        final List<AnnouncementResponse> response = announcementService.findAllAnnouncement();
        return ResponseEntity.ok(response);
    }
}
