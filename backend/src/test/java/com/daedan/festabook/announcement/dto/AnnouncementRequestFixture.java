package com.daedan.festabook.announcement.dto;

public class AnnouncementRequestFixture {

    private static final String DEFAULT_TITLE = "폭우가 내립니다.";
    private static final String DEFAULT_CONTENT = "우산을 챙겨주세요.";
    private static final boolean DEFAULT_IS_PINNED = false;

    public static AnnouncementRequest create() {
        return new AnnouncementRequest(
                DEFAULT_TITLE,
                DEFAULT_CONTENT,
                DEFAULT_IS_PINNED
        );
    }

    public static AnnouncementRequest createWithTitle(
            String title
    ) {
        return new AnnouncementRequest(
                title,
                DEFAULT_CONTENT,
                DEFAULT_IS_PINNED
        );
    }

    public static AnnouncementRequest createWithContent(
            String content
    ) {
        return new AnnouncementRequest(
                DEFAULT_TITLE,
                content,
                DEFAULT_IS_PINNED
        );
    }

    public static AnnouncementRequest create(
            boolean isPinned
    ) {
        return new AnnouncementRequest(
                DEFAULT_TITLE,
                DEFAULT_CONTENT,
                isPinned
        );
    }

    public static AnnouncementRequest create(
            String title,
            String content,
            boolean isPinned
    ) {
        return new AnnouncementRequest(
                title,
                content,
                isPinned
        );
    }
}
