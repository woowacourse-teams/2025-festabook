package com.daedan.festabook.place.domain;

public class PlaceAnnouncementFixture {

    private static final Place DEFAULT_PLACE = PlaceFixture.create();
    private static final String DEFAULT_TITLE = "치킨 재고 소진되었습니다.";
    private static final String DEFAULT_CONTENT = "앞으로 더 좋은 주점으로 찾아뵙겠습니다.";

    public static PlaceAnnouncement create() {
        return new PlaceAnnouncement(
                DEFAULT_PLACE,
                DEFAULT_TITLE,
                DEFAULT_CONTENT
        );
    }

    public static PlaceAnnouncement create(
            Place place
    ) {
        return new PlaceAnnouncement(
                place,
                DEFAULT_TITLE,
                DEFAULT_CONTENT
        );
    }

    public static PlaceAnnouncement create(
            Place place,
            String title,
            String content
    ) {
        return new PlaceAnnouncement(place, title, content);
    }
}
