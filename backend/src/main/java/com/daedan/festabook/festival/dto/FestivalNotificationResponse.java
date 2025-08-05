package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.FestivalNotification;

public record FestivalNotificationResponse(
        Long id
) {

    public static FestivalNotificationResponse from(FestivalNotification festivalNotification) {
        return new FestivalNotificationResponse(
                festivalNotification.getId()
        );
    }
}
