package com.daedan.festabook.device.service;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.dto.DeviceRequest;
import com.daedan.festabook.device.dto.DeviceResponse;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceJpaRepository deviceJpaRepository;

    @Transactional
    public DeviceResponse registerDevice(DeviceRequest request) {
        Device device = deviceJpaRepository.findByDeviceIdentifier(request.deviceIdentifier())
                .orElseGet(() -> saveNewDevice(request));
        return DeviceResponse.from(device);
    }

    private Device saveNewDevice(DeviceRequest request) {
        Device newDevice = request.toEntity();
        return deviceJpaRepository.save(newDevice);
    }
}
