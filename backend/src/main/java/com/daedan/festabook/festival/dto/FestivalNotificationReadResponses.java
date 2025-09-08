package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.FestivalNotification;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record FestivalNotificationReadResponses(
        @JsonValue List<FestivalNotificationReadResponse> responses
) {

    public static FestivalNotificationReadResponses from(List<FestivalNotification> festivalNotifications) {
        return new FestivalNotificationReadResponses(
                festivalNotifications.stream()
                        .map(FestivalNotificationReadResponse::from)
                        .toList()
        );
    }
}
