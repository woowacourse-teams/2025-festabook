package com.daedan.festabook.device.dto;

public class DeviceRequestFixture {

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
