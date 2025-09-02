package com.daedan.festabook.festival.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.domain.FestivalNotification;
import com.daedan.festabook.festival.domain.FestivalNotificationFixture;
import com.daedan.festabook.festival.domain.FestivalNotificationManager;
import com.daedan.festabook.festival.dto.FestivalNotificationRequest;
import com.daedan.festabook.festival.dto.FestivalNotificationRequestFixture;
import com.daedan.festabook.festival.dto.FestivalNotificationResponse;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.festival.infrastructure.FestivalNotificationJpaRepository;
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
class FestivalNotificationServiceTest {

    @Mock
    private FestivalJpaRepository festivalJpaRepository;

    @Mock
    private FestivalNotificationJpaRepository festivalNotificationJpaRepository;

    @Mock
    private DeviceJpaRepository deviceJpaRepository;

    @Mock
    private FestivalNotificationManager festivalNotificationManager;

    @InjectMocks
    private FestivalNotificationService festivalNotificationService;

    @Nested
    class subscribeFestivalNotification {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Long deviceId = 10L;
            Device device = DeviceFixture.create(deviceId);
            Long festivalNotificationId = 100L;
            FestivalNotification festivalNotification = FestivalNotificationFixture.create(
                    festivalNotificationId,
                    festival,
                    device
            );
            FestivalNotificationRequest request = FestivalNotificationRequestFixture.create(deviceId);

            given(festivalJpaRepository.findById(festivalId))
                    .willReturn(Optional.of(festival));
            given(deviceJpaRepository.findById(deviceId))
                    .willReturn(Optional.of(device));
            given(festivalNotificationJpaRepository.save(any()))
                    .willReturn(festivalNotification);

            // when
            FestivalNotificationResponse result = festivalNotificationService.subscribeFestivalNotification(
                    festivalId, request);

            // then
            assertThat(result.festivalNotificationId()).isEqualTo(festivalNotificationId);
            then(festivalNotificationJpaRepository).should()
                    .save(any());
            then(festivalNotificationManager).should()
                    .subscribeFestivalTopic(any(), any());
        }

        @Test
        void 예외_축제에_이미_알림을_구독한_디바이스() {
            // given
            Long festivalId = 1L;
            Long deviceId = 1L;

            FestivalNotificationRequest request = FestivalNotificationRequestFixture.create(deviceId);

            given(festivalNotificationJpaRepository.existsByFestivalIdAndDeviceId(festivalId, deviceId))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() ->
                    festivalNotificationService.subscribeFestivalNotification(festivalId, request)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미 알림을 구독한 축제입니다.");
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            Long invalidDeviceId = 0L;
            FestivalNotificationRequest request = FestivalNotificationRequestFixture.create(invalidDeviceId);

            Long festivalId = 1L;
            given(festivalJpaRepository.findById(festivalId))
                    .willReturn(Optional.of(FestivalFixture.create()));
            given(deviceJpaRepository.findById(invalidDeviceId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    festivalNotificationService.subscribeFestivalNotification(festivalId, request)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 디바이스입니다.");
        }

        @Test
        void 예외_존재하지_않는_축제() {
            // given
            Long deviceId = 10L;
            FestivalNotificationRequest request = FestivalNotificationRequestFixture.create(deviceId);

            Long invalidFestivalId = 0L;
            given(festivalJpaRepository.findById(invalidFestivalId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    festivalNotificationService.subscribeFestivalNotification(invalidFestivalId, request)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 축제입니다.");
        }
    }

    @Nested
    class unsubscribeFestivalNotification {

        @Test
        void 성공() {
            // given
            Long festivalNotificationId = 1L;
            Long deviceId = 10L;
            Long festivalId = 100L;

            Device device = DeviceFixture.create(deviceId);
            Festival festival = FestivalFixture.create(festivalId);
            FestivalNotification festivalNotification = FestivalNotificationFixture.create(
                    festivalNotificationId,
                    festival,
                    device
            );

            given(festivalNotificationJpaRepository.findById(festivalNotificationId))
                    .willReturn(Optional.of(festivalNotification));
            given(deviceJpaRepository.findById(deviceId))
                    .willReturn(Optional.of(device));

            // when
            festivalNotificationService.unsubscribeFestivalNotification(festivalNotificationId);

            // then
            then(festivalNotificationJpaRepository).should()
                    .deleteById(festivalNotificationId);
            then(festivalNotificationManager).should()
                    .unsubscribeFestivalTopic(festivalId, device.getFcmToken());
        }

        @Test
        void 성공_알림_삭제시_축제_알림이_존재하지_않아도_정상_처리() {
            // given
            Long invalidFestivalNotificationId = 0L;

            given(festivalNotificationJpaRepository.findById(invalidFestivalNotificationId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatCode(() ->
                    festivalNotificationService.unsubscribeFestivalNotification(
                            invalidFestivalNotificationId
                    )
            )
                    .doesNotThrowAnyException();
            then(festivalNotificationManager)
                    .shouldHaveNoInteractions();
        }

        @Test
        void 성공_알림_삭제시_디바이스가_존재하지_않아도_정상_처리() {
            // given
            Long festivalNotificationId = 1L;
            Long invalidDeviceId = 0L;
            Long festivalId = 100L;

            Device device = DeviceFixture.create(invalidDeviceId);
            Festival festival = FestivalFixture.create(festivalId);
            FestivalNotification festivalNotification = FestivalNotificationFixture.create(
                    festivalNotificationId,
                    festival,
                    device
            );

            given(festivalNotificationJpaRepository.findById(festivalNotificationId))
                    .willReturn(Optional.of(festivalNotification));
            given(deviceJpaRepository.findById(invalidDeviceId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatCode(() ->
                    festivalNotificationService.unsubscribeFestivalNotification(festivalNotificationId)
            )
                    .doesNotThrowAnyException();
            then(festivalNotificationManager)
                    .shouldHaveNoInteractions();
        }
    }
}
