package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.FestivalNotification;

public record FestivalNotificationResponse(
        Long festivalNotificationId
) {

    public static FestivalNotificationResponse from(FestivalNotification festivalNotification) {
        return new FestivalNotificationResponse(
                festivalNotification.getId()
        );
    }
}
