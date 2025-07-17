package com.daedan.festabook.device.service;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.dto.DeviceRequest;
import com.daedan.festabook.device.dto.DeviceResponse;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceJpaRepository deviceJpaRepository;

    public DeviceResponse createDevice(DeviceRequest request) {
        Device device = request.toEntity();
        deviceJpaRepository.save(device);

        return DeviceResponse.from(device);
    }
}
