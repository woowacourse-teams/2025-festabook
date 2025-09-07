package com.daedan.festabook.device.dto;

import com.daedan.festabook.device.domain.Device;

public record DeviceResponse(
        Long deviceId
) {

    public static DeviceResponse from(Device device) {
        return new DeviceResponse(
                device.getId()
        );
    }
}
