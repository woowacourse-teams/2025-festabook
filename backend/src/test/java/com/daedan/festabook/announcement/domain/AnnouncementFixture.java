package com.daedan.festabook.announcement.domain;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AnnouncementFixture {

    private static final Organization DEFAULT_ORGANIZATION = OrganizationFixture.create();
    private static final String DEFAULT_TITLE = "폭우가 내립니다.";
    private static final String DEFAULT_CONTENT = "우산을 챙겨주세요.";
    private static final boolean DEFAULT_IS_PINNED = false;

    public static Announcement create() {
        return new Announcement(DEFAULT_TITLE, DEFAULT_CONTENT, DEFAULT_IS_PINNED, DEFAULT_ORGANIZATION);
    }

    public static Announcement create(
            Organization organization
    ) {
        return new Announcement(DEFAULT_TITLE, DEFAULT_CONTENT, DEFAULT_IS_PINNED, organization);
    }

    public static List<Announcement> createList(
            int size,
            Organization organization
    ) {
        return IntStream.range(0, size)
                .mapToObj(i -> create(organization))
                .collect(Collectors.toList());
    }
}
