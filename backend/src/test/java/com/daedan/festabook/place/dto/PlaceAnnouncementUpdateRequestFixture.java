package com.daedan.festabook.place.dto;

public class PlaceAnnouncementUpdateRequestFixture {

    private static final String DEFAULT_TITLE = "치킨 재고 소진되었습니다.";
    private static final String DEFAULT_CONTENT = "앞으로 더 좋은 주점으로 찾아뵙겠습니다.";

    public static PlaceAnnouncementUpdateRequest create(
            String title,
            String content
    ) {
        return new PlaceAnnouncementUpdateRequest(
                title,
                content
        );
    }

    public static PlaceAnnouncementUpdateRequest create() {
        return new PlaceAnnouncementUpdateRequest(
                DEFAULT_TITLE,
                DEFAULT_CONTENT
        );
    }
}
