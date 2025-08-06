package com.daedan.festabook.festival.domain;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;

public class FestivalNotificationFixture {

    private static final Festival DEFAULT_FESTIVAL = FestivalFixture.create();
    private static final Device DEFAULT_DEVICE = DeviceFixture.create();

    public static FestivalNotification create(
            Long id
    ) {
        return new FestivalNotification(
                id,
                DEFAULT_FESTIVAL,
                DEFAULT_DEVICE
        );
    }

    public static FestivalNotification create(
            Long id,
            Festival festival
    ) {
        return new FestivalNotification(
                id,
                festival,
                DEFAULT_DEVICE
        );
    }

    public static FestivalNotification create(
            Long id,
            Festival festival,
            Device device
    ) {
        return new FestivalNotification(
                id,
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
