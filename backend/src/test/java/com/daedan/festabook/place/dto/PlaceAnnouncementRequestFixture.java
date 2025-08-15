package com.daedan.festabook.place.dto;

public class PlaceAnnouncementRequestFixture {

    private static final String DEFAULT_TITLE = "공지 제목입니다. 확인해주세요.";
    private static final String DEFAULT_CONTENT = "공지 내용입니다.";

    public static PlaceAnnouncementRequest create() {
        return new PlaceAnnouncementRequest(
                DEFAULT_TITLE,
                DEFAULT_CONTENT
        );
    }
}
