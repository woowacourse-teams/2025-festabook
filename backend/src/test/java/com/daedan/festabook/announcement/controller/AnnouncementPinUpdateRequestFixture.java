package com.daedan.festabook.announcement.controller;

public class AnnouncementPinUpdateRequestFixture {

    private static final boolean DEFAULT_PINNED = true;

    public static AnnouncementPinUpdateRequest create() {
        return new AnnouncementPinUpdateRequest(
                DEFAULT_PINNED
        );
    }

    public static AnnouncementPinUpdateRequest create(boolean pinned) {
        return new AnnouncementPinUpdateRequest(
                pinned
        );
    }
} 