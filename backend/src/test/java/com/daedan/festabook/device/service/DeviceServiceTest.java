package com.daedan.festabook.device.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;
import com.daedan.festabook.device.domain.DeviceRequestFixture;
import com.daedan.festabook.device.dto.DeviceRequest;
import com.daedan.festabook.device.dto.DeviceResponse;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
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
        void 성공_신규_Device_등록_id_응답() {
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
            assertThat(result.id()).isEqualTo(expectedId);
            then(deviceJpaRepository).should()
                    .save(any());
        }

        @Test
        void 성공_복귀_Device_등록_id_응답() {
            // given
            Long expectedId = 1L;
            Device device = DeviceFixture.create(expectedId);
            DeviceRequest request = DeviceRequestFixture.create(device.getDeviceIdentifier(), device.getFcmToken());

            given(deviceJpaRepository.findByDeviceIdentifier(request.deviceIdentifier()))
                    .willReturn(Optional.of(device));

            // when
            DeviceResponse result = deviceService.registerDevice(request);

            // then
            assertThat(result.id()).isEqualTo(expectedId);
            then(deviceJpaRepository).should(never())
                    .save(any());
        }
    }
}
