package com.daedan.festabook.organization.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.notification.constants.TopicConstants;
import com.daedan.festabook.notification.service.NotificationService;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationBookmark;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.dto.OrganizationBookmarkRequest;
import com.daedan.festabook.organization.dto.OrganizationBookmarkResponse;
import com.daedan.festabook.organization.infrastructure.OrganizationBookmarkJpaRepository;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
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
class OrganizationBookmarkServiceTest {

    @Mock
    private OrganizationJpaRepository organizationJpaRepository;

    @Mock
    private OrganizationBookmarkJpaRepository organizationBookmarkJpaRepository;

    @Mock
    private DeviceJpaRepository deviceJpaRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrganizationBookmarkService organizationBookmarkService;

    @Nested
    class createOrganizationBookmark {

        @Test
        void 성공() {
            // given
            Long organizationId = 1L;
            Organization organization = OrganizationFixture.create(organizationId);
            Long deviceId = 10L;
            Device device = DeviceFixture.create(deviceId);
            OrganizationBookmarkRequest request = new OrganizationBookmarkRequest(deviceId);

            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(organization));
            given(deviceJpaRepository.findById(deviceId))
                    .willReturn(Optional.of(device));

            // when
            OrganizationBookmarkResponse result = organizationBookmarkService.createOrganizationBookmark(
                    organizationId, request);

            // then
            assertThat(result).isNotNull();
            verify(organizationBookmarkJpaRepository).save(any(OrganizationBookmark.class));
            verify(notificationService).subscribeTopic(device.getFcmToken(),
                    TopicConstants.getOrganizationTopicById(organizationId));
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            Long deviceId = 10L;
            OrganizationBookmarkRequest request = new OrganizationBookmarkRequest(deviceId);

            Long organizationId = 1L;
            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(OrganizationFixture.create()));
            given(deviceJpaRepository.findById(deviceId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    organizationBookmarkService.createOrganizationBookmark(organizationId, request)
            ).isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 디바이스입니다.");
        }

        @Test
        void 예외_존재하지_않는_조직() {
            // given
            Long deviceId = 10L;
            OrganizationBookmarkRequest request = new OrganizationBookmarkRequest(deviceId);

            Long organizationId = 1L;
            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    organizationBookmarkService.createOrganizationBookmark(organizationId, request)
            ).isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 조직입니다.");
        }
    }

    @Nested
    class deleteOrganizationBookmark {

        @Test
        void 성공() {
            // given
            Long organizationId = 1L;
            Long deviceId = 10L;
            Device device = DeviceFixture.create(deviceId);
            OrganizationBookmarkRequest request = new OrganizationBookmarkRequest(deviceId);

            given(deviceJpaRepository.findById(deviceId))
                    .willReturn(Optional.of(device));

            // when
            organizationBookmarkService.deleteOrganizationBookmark(organizationId, request);

            // then
            verify(organizationBookmarkJpaRepository).deleteByOrganizationIdAndDeviceId(organizationId, deviceId);
            verify(notificationService).unsubscribeTopic(
                    device.getFcmToken(),
                    TopicConstants.getOrganizationTopicById(organizationId)
            );
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            Long organizationId = 1L;
            Long deviceId = 10L;
            OrganizationBookmarkRequest request = new OrganizationBookmarkRequest(deviceId);

            given(deviceJpaRepository.findById(deviceId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    organizationBookmarkService.deleteOrganizationBookmark(organizationId, request)
            ).isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 디바이스입니다.");
        }
    }
}
