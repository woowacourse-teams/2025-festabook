package com.daedan.festabook.announcement.domain;

import com.daedan.festabook.announcement.dto.AnnouncementUpdateRequest;

public class AnnouncementUpdateRequestFixture {

    private static final String DEFAULT_TITLE = "폭우가 내립니다.";
    private static final String DEFAULT_CONTENT = "우산을 챙겨주세요.";

    public static AnnouncementUpdateRequest create() {
        return new AnnouncementUpdateRequest(
                DEFAULT_TITLE,
                DEFAULT_CONTENT
        );
    }

    public static AnnouncementUpdateRequest create(String title, String content) {
        return new AnnouncementUpdateRequest(
                title,
                content
        );
    }
}
