package com.daedan.festabook.device.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;
import com.daedan.festabook.device.dto.DeviceRequest;
import com.daedan.festabook.device.dto.DeviceResponse;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
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
    class createDevice {

        @Test
        void 성공() {
            // given
            Long id = 1L;

            DeviceRequest request = mock(DeviceRequest.class);
            Device device = DeviceFixture.create(id);

            given(request.toEntity())
                    .willReturn(device);

            // when
            DeviceResponse result = deviceService.createDevice(request);

            // then
            assertThat(result.id()).isEqualTo(id);
        }
    }
}
