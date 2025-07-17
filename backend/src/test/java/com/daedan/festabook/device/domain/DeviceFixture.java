package com.daedan.festabook.device.domain;

public class DeviceFixture {

    private static final String DEFAULT_FCM_TOKEN = "e4Jse...";

    public static Device create() {
        return new Device(
                DEFAULT_FCM_TOKEN
        );
    }

    public static Device create(
            Long id
    ) {
        return new Device(
                id,
                DEFAULT_FCM_TOKEN
        );
    }
}
