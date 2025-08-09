package com.daedan.festabook.festival.domain;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;

public class FestivalNotificationFixture {

    private static final Festival DEFAULT_FESTIVAL = FestivalFixture.create();
    private static final Device DEFAULT_DEVICE = DeviceFixture.create();

    public static FestivalNotification create(
            Long festivalNotificationId
    ) {
        return new FestivalNotification(
                festivalNotificationId,
                DEFAULT_FESTIVAL,
                DEFAULT_DEVICE
        );
    }

    public static FestivalNotification create(
            Long festivalNotificationId,
            Festival festival
    ) {
        return new FestivalNotification(
                festivalNotificationId,
                festival,
                DEFAULT_DEVICE
        );
    }

    public static FestivalNotification create(
            Long festivalNotificationId,
            Festival festival,
            Device device
    ) {
        return new FestivalNotification(
                festivalNotificationId,
                festival,
                device
        );
    }

    public static FestivalNotification create(
            Festival festival,
            Device device
    ) {
        return new FestivalNotification(
                festival,
                device
        );
    }
}
