package com.daedan.festabook.festival.domain;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.global.fixture.BaseEntityTestHelper;

public class FestivalNotificationFixture {

    public static FestivalNotification create(
            Festival festival,
            Device device
    ) {
        return new FestivalNotification(
                festival,
                device
        );
    }

    public static FestivalNotification create(
            Festival festival,
            Device device,
            Long festivalNotificationId
    ) {
        FestivalNotification festivalNotification = new FestivalNotification(
                festival,
                device
        );
        BaseEntityTestHelper.setId(festivalNotification, festivalNotificationId);
        return festivalNotification;
    }
}
