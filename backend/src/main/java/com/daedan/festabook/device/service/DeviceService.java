package com.daedan.festabook.device.service;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.dto.DeviceRequest;
import com.daedan.festabook.device.dto.DeviceResponse;
import com.daedan.festabook.device.dto.DeviceUpdateRequest;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @Transactional
    public DeviceResponse updateDevice(Long deviceId, DeviceUpdateRequest request) {
        Device device = getDeviceById(deviceId);
        device.updateDevice(request.fcmToken());

        return DeviceResponse.from(device);
    }

    private Device saveNewDevice(DeviceRequest request) {
        Device newDevice = request.toEntity();
        return deviceJpaRepository.save(newDevice);
    }

    private Device getDeviceById(Long deviceId) {
        return deviceJpaRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 디바이스입니다.", HttpStatus.BAD_REQUEST));
    }
}
