package com.daedan.festabook.device.dto;

public class DeviceUpdateRequestFixture {

    private static final String DEFAULT_FCM_TOKEN = "dummy";

    public static DeviceUpdateRequest create() {
        return new DeviceUpdateRequest(
                DEFAULT_FCM_TOKEN
        );
    }

    public static DeviceUpdateRequest create(
            String fcmToken
    ) {
        return new DeviceUpdateRequest(
                fcmToken
        );
    }
}
