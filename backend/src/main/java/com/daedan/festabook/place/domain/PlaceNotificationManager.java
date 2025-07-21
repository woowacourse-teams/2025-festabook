package com.daedan.festabook.place.domain;

public interface PlaceNotificationManager {

    void subscribePlaceTopic(Long placeId, String token);

    void unsubscribePlaceTopic(Long placeId, String token);
}
