package com.daedan.festabook.device.domain;

import com.daedan.festabook.global.fixture.BaseEntityTestHelper;

public class DeviceFixture {

    private static final String DEFAULT_DEVICE_IDENTIFIER = "f47ac10b...";
    private static final String DEFAULT_FCM_TOKEN = "e4Jse...";

    public static Device create() {
        return new Device(
                DEFAULT_DEVICE_IDENTIFIER,
                DEFAULT_FCM_TOKEN
        );
    }

    public static Device createWithDeviceIdentifier(
            String deviceIdentifier
    ) {
        return new Device(
                deviceIdentifier,
                DEFAULT_FCM_TOKEN
        );
    }

    public static Device createWithFcmToken(
            String fcmToken
    ) {
        return new Device(
                DEFAULT_DEVICE_IDENTIFIER,
                fcmToken
        );
    }

    public static Device create(
            Long deviceId
    ) {
        Device device = new Device(
                DEFAULT_DEVICE_IDENTIFIER,
                DEFAULT_FCM_TOKEN
        );
        BaseEntityTestHelper.setId(device, deviceId);
        return device;
    }
}
