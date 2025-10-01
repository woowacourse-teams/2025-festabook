package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.FestivalNotification;

public record FestivalNotificationReadResponse(
        Long festivalNotificationId,
        Long festivalId,
        String universityName,
        String festivalName
) {

    public static FestivalNotificationReadResponse from(FestivalNotification festivalNotification) {
        return new FestivalNotificationReadResponse(
                festivalNotification.getId(),
                festivalNotification.getFestival().getId(),
                festivalNotification.getFestival().getUniversityName(),
                festivalNotification.getFestival().getFestivalName()
        );
    }
}
