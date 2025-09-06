package com.daedan.festabook.announcement.domain;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.global.fixture.BaseEntityTestHelper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AnnouncementFixture {

    private static final String DEFAULT_TITLE = "폭우가 내립니다.";
    private static final String DEFAULT_CONTENT = "우산을 챙겨주세요.";
    private static final boolean DEFAULT_IS_PINNED = false;
    private static final Festival DEFAULT_FESTIVAL = FestivalFixture.create();
    private static final LocalDateTime DEFAULT_CREATED_AT = LocalDateTime.now();

    public static Announcement create(
            boolean isPinned
    ) {
        Announcement announcement = new Announcement(
                DEFAULT_TITLE,
                DEFAULT_CONTENT,
                isPinned,
                DEFAULT_FESTIVAL
        );
        BaseEntityTestHelper.setCreatedAt(announcement, DEFAULT_CREATED_AT);
        return announcement;
    }

    public static Announcement create(
            Festival festival
    ) {
        return new Announcement(
                DEFAULT_TITLE,
                DEFAULT_CONTENT,
                DEFAULT_IS_PINNED,
                festival
        );
    }

    public static Announcement create(
            boolean isPinned,
            LocalDateTime createdAt
    ) {
        Announcement announcement = new Announcement(
                DEFAULT_TITLE,
                DEFAULT_CONTENT,
                isPinned,
                DEFAULT_FESTIVAL
        );
        BaseEntityTestHelper.setCreatedAt(announcement, createdAt);
        return announcement;
    }

    public static Announcement create(
            Festival festival,
            Long announcementId
    ) {
        Announcement announcement = new Announcement(
                DEFAULT_TITLE,
                DEFAULT_CONTENT,
                DEFAULT_IS_PINNED,
                festival
        );
        BaseEntityTestHelper.setId(announcement, announcementId);
        return announcement;
    }

    public static Announcement create(
            boolean isPinned,
            Festival festival
    ) {
        return new Announcement(
                DEFAULT_TITLE,
                DEFAULT_CONTENT,
                isPinned,
                festival
        );
    }

    public static Announcement create(
            boolean isPinned,
            Festival festival,
            Long announcementId
    ) {
        Announcement announcement = new Announcement(
                DEFAULT_TITLE,
                DEFAULT_CONTENT,
                isPinned,
                festival
        );
        BaseEntityTestHelper.setId(announcement, announcementId);
        return announcement;
    }

    public static Announcement create(
            String title,
            String content,
            boolean isPinned,
            Festival festival
    ) {
        return new Announcement(
                title,
                content,
                isPinned,
                festival
        );
    }

    public static Announcement createWithTitle(
            String title
    ) {
        return new Announcement(
                title,
                DEFAULT_CONTENT,
                DEFAULT_IS_PINNED,
                DEFAULT_FESTIVAL
        );
    }

    public static Announcement createWithContent(
            String content
    ) {
        return new Announcement(
                DEFAULT_TITLE,
                content,
                DEFAULT_IS_PINNED,
                DEFAULT_FESTIVAL
        );
    }

    public static List<Announcement> createList(
            int size,
            boolean isPinned
    ) {
        return IntStream.range(0, size)
                .mapToObj(i -> create(isPinned))
                .collect(Collectors.toList());
    }

    public static List<Announcement> createList(
            int size,
            boolean isPinned,
            Festival festival
    ) {
        return IntStream.range(0, size)
                .mapToObj(i -> create(isPinned, festival))
                .collect(Collectors.toList());
    }
}
