package com.daedan.festabook.device.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;
import com.daedan.festabook.device.dto.DeviceRequest;
import com.daedan.festabook.device.dto.DeviceRequestFixture;
import com.daedan.festabook.device.dto.DeviceResponse;
import com.daedan.festabook.device.dto.DeviceUpdateRequest;
import com.daedan.festabook.device.dto.DeviceUpdateRequestFixture;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DeviceServiceTest {

    @Mock
    private DeviceJpaRepository deviceJpaRepository;

    @InjectMocks
    private DeviceService deviceService;

    @Nested
    class registerDevice {

        @Test
        void 성공_Device_등록() {
            // given
            Long expectedId = 1L;
            Device device = DeviceFixture.create(expectedId);
            DeviceRequest request = DeviceRequestFixture.create(device.getDeviceIdentifier(), device.getFcmToken());

            given(deviceJpaRepository.findByDeviceIdentifier(request.deviceIdentifier()))
                    .willReturn(Optional.empty());
            given(deviceJpaRepository.save(any()))
                    .willReturn(device);

            // when
            DeviceResponse result = deviceService.registerDevice(request);

            // then
            assertThat(result.deviceId()).isEqualTo(expectedId);
            then(deviceJpaRepository).should()
                    .save(any());
        }

        @Test
        void 성공_기존에_존재하는_Device() {
            // given
            Long expectedId = 1L;
            Device device = DeviceFixture.create(expectedId);
            DeviceRequest request = DeviceRequestFixture.create(device.getDeviceIdentifier(), device.getFcmToken());

            given(deviceJpaRepository.findByDeviceIdentifier(request.deviceIdentifier()))
                    .willReturn(Optional.of(device));

            // when
            DeviceResponse result = deviceService.registerDevice(request);

            // then
            assertThat(result.deviceId()).isEqualTo(expectedId);
            then(deviceJpaRepository).should(never())
                    .save(any());
        }
    }

    @Nested
    class updateDevice {

        @Test
        void 성공() {
            // given
            Long deviceId = 1L;
            Device device = DeviceFixture.create(deviceId);

            given(deviceJpaRepository.findById(device.getId()))
                    .willReturn(Optional.of(device));

            DeviceUpdateRequest request = DeviceUpdateRequestFixture.create();

            // when
            DeviceResponse result = deviceService.updateDevice(device.getId(), request);

            // then
            assertThat(result.deviceId()).isEqualTo(deviceId);
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            Long invalidDeviceId = 0L;
            Device device = DeviceFixture.create(invalidDeviceId);
            DeviceUpdateRequest request = DeviceUpdateRequestFixture.create();

            given(deviceJpaRepository.findById(invalidDeviceId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> deviceService.updateDevice(device.getId(), request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 디바이스입니다.");
        }
    }
}
