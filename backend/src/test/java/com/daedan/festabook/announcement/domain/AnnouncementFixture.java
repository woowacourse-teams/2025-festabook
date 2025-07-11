package com.daedan.festabook.announcement.domain;

public class AnnouncementFixture {

    private static final String DEFAULT_TITLE = "폭우가 내립니다.";
    private static final String DEFAULT_CONTENT = "우산을 챙겨주세요.";

    public static Announcement create() {
        return new Announcement(
                DEFAULT_TITLE,
                DEFAULT_CONTENT
        );
    }
}
