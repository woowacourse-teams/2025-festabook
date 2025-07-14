package com.daedan.festabook.announcement.domain;

import com.daedan.festabook.organization.domain.Organization;

public class AnnouncementFixture {

    // TODO: OrganizationFixture로 변경
    private static final Organization DEFAULT_ORGANIZATION = new Organization("서울대학교");
    private static final String DEFAULT_TITLE = "폭우가 내립니다.";
    private static final String DEFAULT_CONTENT = "우산을 챙겨주세요.";

    public static Announcement create() {
        return new Announcement(
                DEFAULT_TITLE,
                DEFAULT_CONTENT,
                DEFAULT_ORGANIZATION
        );
    }
}
