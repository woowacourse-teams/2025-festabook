package com.daedan.festabook.announcement.service;

import com.daedan.festabook.announcement.dto.AnnouncementResponses;
import com.daedan.festabook.announcement.infrastructure.AnnouncementJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementJpaRepository announcementJpaRepository;

    public AnnouncementResponses findAllAnnouncement() {
        return AnnouncementResponses.from(announcementJpaRepository.findAll());
    }
}
