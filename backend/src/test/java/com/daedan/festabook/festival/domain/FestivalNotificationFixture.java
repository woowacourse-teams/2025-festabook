package com.daedan.festabook.festival.domain;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;
import com.daedan.festabook.global.fixture.BaseEntityTestHelper;

public class FestivalNotificationFixture {

    private static final Festival DEFAULT_FESTIVAL = FestivalFixture.create();
    private static final Device DEFAULT_DEVICE = DeviceFixture.create();

    public static FestivalNotification create(
            Long festivalNotificationId
    ) {
        FestivalNotification festivalNotification = new FestivalNotification(
                null,
                DEFAULT_FESTIVAL,
                DEFAULT_DEVICE
        );
        BaseEntityTestHelper.setId(festivalNotification, festivalNotificationId);
        return festivalNotification;
    }

    public static FestivalNotification create(
            Long festivalNotificationId,
            Festival festival
    ) {
        FestivalNotification festivalNotification = new FestivalNotification(
                null,
                festival,
                DEFAULT_DEVICE
        );
        BaseEntityTestHelper.setId(festivalNotification, festivalNotificationId);
        return festivalNotification;
    }

    public static FestivalNotification create(
            Long festivalNotificationId,
            Festival festival,
            Device device
    ) {
        FestivalNotification festivalNotification = new FestivalNotification(
                null,
                festival,
                device
        );
        BaseEntityTestHelper.setId(festivalNotification, festivalNotificationId);
        return festivalNotification;
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
