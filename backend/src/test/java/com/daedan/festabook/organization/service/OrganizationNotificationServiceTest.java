package com.daedan.festabook.organization.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.domain.OrganizationNotification;
import com.daedan.festabook.organization.domain.OrganizationNotificationFixture;
import com.daedan.festabook.organization.domain.OrganizationNotificationManager;
import com.daedan.festabook.organization.domain.OrganizationNotificationRequestFixture;
import com.daedan.festabook.organization.dto.OrganizationNotificationRequest;
import com.daedan.festabook.organization.dto.OrganizationNotificationResponse;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.organization.infrastructure.OrganizationNotificationJpaRepository;
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
class OrganizationNotificationServiceTest {

    @Mock
    private OrganizationJpaRepository organizationJpaRepository;

    @Mock
    private OrganizationNotificationJpaRepository organizationNotificationJpaRepository;

    @Mock
    private DeviceJpaRepository deviceJpaRepository;

    @Mock
    private OrganizationNotificationManager organizationNotificationManager;

    @InjectMocks
    private OrganizationNotificationService organizationNotificationService;

    @Nested
    class subscribeOrganizationNotification {

        @Test
        void 성공() {
            // given
            Long organizationId = 1L;
            Organization organization = OrganizationFixture.create(organizationId);
            Long deviceId = 10L;
            Device device = DeviceFixture.create(deviceId);
            Long organizationNotificationId = 100L;
            OrganizationNotification organizationNotification = OrganizationNotificationFixture.create(
                    organizationNotificationId,
                    organization,
                    device
            );
            OrganizationNotificationRequest request = OrganizationNotificationRequestFixture.create(deviceId);

            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(organization));
            given(deviceJpaRepository.findById(deviceId))
                    .willReturn(Optional.of(device));
            given(organizationNotificationJpaRepository.save(any()))
                    .willReturn(organizationNotification);

            // when
            OrganizationNotificationResponse result = organizationNotificationService.subscribeOrganizationNotification(
                    organizationId, request);

            // then
            assertThat(result.id()).isEqualTo(organizationNotificationId);
            then(organizationNotificationJpaRepository).should()
                    .save(any());
            then(organizationNotificationManager).should()
                    .subscribeOrganizationTopic(any(), any());
        }

        @Test
        void 예외_조직에_이미_알림을_구독한_디바이스() {
            // given
            Long organizationId = 1L;
            Long deviceId = 1L;

            OrganizationNotificationRequest request = OrganizationNotificationRequestFixture.create(deviceId);

            given(organizationNotificationJpaRepository.existsByOrganizationIdAndDeviceId(organizationId, deviceId))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> organizationNotificationService.subscribeOrganizationNotification(
                    organizationId,
                    request
            ))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미 알림을 구독한 조직입니다.");
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            Long invalidDeviceId = 0L;
            OrganizationNotificationRequest request = OrganizationNotificationRequestFixture.create(invalidDeviceId);

            Long organizationId = 1L;
            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(OrganizationFixture.create()));
            given(deviceJpaRepository.findById(invalidDeviceId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    organizationNotificationService.subscribeOrganizationNotification(organizationId, request)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 디바이스입니다.");
        }

        @Test
        void 예외_존재하지_않는_조직() {
            // given
            Long deviceId = 10L;
            OrganizationNotificationRequest request = OrganizationNotificationRequestFixture.create(deviceId);

            Long invalidOrganizationId = 0L;
            given(organizationJpaRepository.findById(invalidOrganizationId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    organizationNotificationService.subscribeOrganizationNotification(invalidOrganizationId, request)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 조직입니다.");
        }
    }

    @Nested
    class unsubscribeOrganizationNotification {

        @Test
        void 성공() {
            // given
            Long organizationNotificationId = 1L;
            Long deviceId = 10L;
            Long organizationId = 100L;

            Device device = DeviceFixture.create(deviceId);
            Organization organization = OrganizationFixture.create(organizationId);
            OrganizationNotification organizationNotification = OrganizationNotificationFixture.create(
                    organizationNotificationId,
                    organization,
                    device
            );

            given(organizationNotificationJpaRepository.findById(organizationNotificationId))
                    .willReturn(Optional.of(organizationNotification));
            given(deviceJpaRepository.findById(deviceId))
                    .willReturn(Optional.of(device));

            // when
            organizationNotificationService.unsubscribeOrganizationNotification(organizationNotificationId);

            // then
            then(organizationNotificationJpaRepository).should()
                    .deleteById(organizationNotificationId);
            then(organizationNotificationManager).should()
                    .unsubscribeOrganizationTopic(organizationId, device.getFcmToken());
        }

        @Test
        void 성공_알림_삭제시_조직_알림이_존재하지_않아도_정상_처리() {
            // given
            Long invalidOrganizationNotificationId = 0L;

            given(organizationNotificationJpaRepository.findById(invalidOrganizationNotificationId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatCode(() -> organizationNotificationService.unsubscribeOrganizationNotification(
                    invalidOrganizationNotificationId
            ))
                    .doesNotThrowAnyException();
            then(organizationNotificationManager)
                    .shouldHaveNoInteractions();
        }

        @Test
        void 성공_알림_삭제시_디바이스가_존재하지_않아도_정상_처리() {
            // given
            Long organizationNotificationId = 1L;
            Long invalidDeviceId = 0L;
            Long organizationId = 100L;

            Device device = DeviceFixture.create(invalidDeviceId);
            Organization organization = OrganizationFixture.create(organizationId);
            OrganizationNotification organizationNotification = OrganizationNotificationFixture.create(
                    organizationNotificationId,
                    organization,
                    device
            );

            given(organizationNotificationJpaRepository.findById(organizationNotificationId))
                    .willReturn(Optional.of(organizationNotification));
            given(deviceJpaRepository.findById(invalidDeviceId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatCode(() -> organizationNotificationService.unsubscribeOrganizationNotification(
                    organizationNotificationId))
                    .doesNotThrowAnyException();
            then(organizationNotificationManager)
                    .shouldHaveNoInteractions();
        }
    }
}
