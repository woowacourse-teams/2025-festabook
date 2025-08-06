package com.daedan.festabook.festival.dto;

public class FestivalNotificationRequestFixture {

    public static FestivalNotificationRequest create(
            Long deviceId
    ) {
        return new FestivalNotificationRequest(
                deviceId
        );
    }
}
