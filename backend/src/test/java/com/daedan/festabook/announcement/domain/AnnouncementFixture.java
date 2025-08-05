package com.daedan.festabook.announcement.domain;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AnnouncementFixture {

    private static final String DEFAULT_TITLE = "폭우가 내립니다.";
    private static final String DEFAULT_CONTENT = "우산을 챙겨주세요.";
    private static final boolean DEFAULT_IS_PINNED = false;
    private static final Organization DEFAULT_ORGANIZATION = OrganizationFixture.create();
    private static final LocalDateTime DEFAULT_CREATED_AT = LocalDateTime.now();

    public static Announcement create() {
        return new Announcement(DEFAULT_TITLE, DEFAULT_CONTENT, DEFAULT_IS_PINNED, DEFAULT_ORGANIZATION);
    }

    public static Announcement create(
            boolean isPinned
    ) {
        return new Announcement(null, DEFAULT_TITLE, DEFAULT_CONTENT, isPinned, DEFAULT_ORGANIZATION,
                DEFAULT_CREATED_AT);
    }

    public static Announcement create(
            Organization organization
    ) {
        return new Announcement(DEFAULT_TITLE, DEFAULT_CONTENT, DEFAULT_IS_PINNED, organization);
    }

    public static Announcement create(
            Long announcementId,
            boolean isPinned
    ) {
        return new Announcement(announcementId, DEFAULT_TITLE, DEFAULT_CONTENT, isPinned, DEFAULT_ORGANIZATION,
                DEFAULT_CREATED_AT);
    }

    public static Announcement create(
            boolean isPinned,
            LocalDateTime createdAt
    ) {
        return new Announcement(null, DEFAULT_TITLE, DEFAULT_CONTENT, isPinned, DEFAULT_ORGANIZATION, createdAt);
    }

    public static Announcement create(
            boolean isPinned,
            Organization organization
    ) {
        return new Announcement(DEFAULT_TITLE, DEFAULT_CONTENT, isPinned, organization);
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
            Organization organization
    ) {
        return IntStream.range(0, size)
                .mapToObj(i -> create(isPinned, organization))
                .collect(Collectors.toList());
    }
}
