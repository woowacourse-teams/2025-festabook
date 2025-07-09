package com.daedan.festabook.announcement.service;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.dto.AnnouncementRequest;
import com.daedan.festabook.announcement.dto.AnnouncementResponse;
import com.daedan.festabook.announcement.infrastructure.AnnouncementJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementJpaRepository announcementJpaRepository;

    public AnnouncementResponse createAnnouncement(AnnouncementRequest request) {
        Announcement notSavedAnnouncement = Announcement.builder()
                .title(request.title())
                .content(request.content())
                .build();
        Announcement savedAnnouncement = announcementJpaRepository.save(notSavedAnnouncement);
        return AnnouncementResponse.from(savedAnnouncement);
    }

    public List<AnnouncementResponse> findAllAnnouncement() {
        return announcementJpaRepository.findAll().stream()
                .map(AnnouncementResponse::from)
                .toList();
    }
}
