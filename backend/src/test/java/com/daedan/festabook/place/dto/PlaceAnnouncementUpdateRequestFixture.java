package com.daedan.festabook.place.dto;

public class PlaceAnnouncementUpdateRequestFixture {

    public static PlaceAnnouncementUpdateRequest create(
            String title,
            String content
    ) {
        return new PlaceAnnouncementUpdateRequest(
                title,
                content
        );
    }
}
