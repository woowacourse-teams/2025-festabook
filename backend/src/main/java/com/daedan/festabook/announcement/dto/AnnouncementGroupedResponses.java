package com.daedan.festabook.announcement.dto;

import com.daedan.festabook.announcement.domain.Announcement;
import java.util.List;

public record AnnouncementGroupedResponses(
        AnnouncementResponses pinned,
        AnnouncementResponses unpinned
) {

    public static AnnouncementGroupedResponses from(List<Announcement> pinnedAnnouncements,
                                                    List<Announcement> unpinnedAnnouncements) {
        return new AnnouncementGroupedResponses(
                AnnouncementResponses.from(pinnedAnnouncements),
                AnnouncementResponses.from(unpinnedAnnouncements)
        );
    }
}
