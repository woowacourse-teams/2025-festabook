package com.daedan.festabook.announcement.service;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.domain.DateTimeGenerator;
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
    private final DateTimeGenerator dateTimeGenerator;

    public AnnouncementResponse createAnnouncement(final AnnouncementRequest request) {

        final Announcement notSavedAnnouncement = Announcement.builder()
                .title(request.title())
                .date(dateTimeGenerator.generateDate())
                .time(dateTimeGenerator.generateTime())
                .content(request.content())
                .build();
        final Announcement savedAnnouncement = announcementJpaRepository.save(notSavedAnnouncement);
        return AnnouncementResponse.from(savedAnnouncement);
    }

    public List<AnnouncementResponse> findAllAnnouncement() {
        return announcementJpaRepository.findAll().stream()
                .map(AnnouncementResponse::from)
                .toList();
    }

}
