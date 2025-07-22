package com.daedan.festabook.device.domain;

import com.daedan.festabook.device.dto.DeviceRequest;

public class DeviceRequestFixture {

    private static final String DEFAULT_DEVICE_IDENTIFIER = "test-device-identifier-001";
    private static final String DEFAULT_FCM_TOKEN = "test-fcm-token-001";

    public static DeviceRequest create() {
        return new DeviceRequest(
                DEFAULT_DEVICE_IDENTIFIER,
                DEFAULT_FCM_TOKEN
        );
    }

    public static DeviceRequest create(
            String deviceIdentifier,
            String fcmToken
    ) {
        return new DeviceRequest(
                deviceIdentifier,
                fcmToken
        );
    }
}
