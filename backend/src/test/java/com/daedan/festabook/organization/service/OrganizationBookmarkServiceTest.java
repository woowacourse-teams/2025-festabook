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

    private static final Long ORGANIZATION_ID = 1L;
    private static final Long DEVICE_ID = 10L;

    @Nested
    class createOrganizationBookmark {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            Device device = DeviceFixture.create(DEVICE_ID);
            OrganizationBookmarkRequest request = new OrganizationBookmarkRequest(DEVICE_ID);

            given(organizationJpaRepository.findById(ORGANIZATION_ID))
                    .willReturn(Optional.of(organization));
            given(deviceJpaRepository.findById(DEVICE_ID))
                    .willReturn(Optional.of(device));

            // when
            OrganizationBookmarkResponse result = organizationBookmarkService.createOrganizationBookmark(
                    ORGANIZATION_ID, request);

            // then
            assertThat(result).isNotNull();
            verify(organizationBookmarkJpaRepository).save(any(OrganizationBookmark.class));
            verify(notificationService).subscribeTopic(device.getFcmToken(),
                    TopicConstants.getOrganizationTopicById(ORGANIZATION_ID));
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            OrganizationBookmarkRequest request = new OrganizationBookmarkRequest(DEVICE_ID);

            given(organizationJpaRepository.findById(ORGANIZATION_ID))
                    .willReturn(Optional.of(OrganizationFixture.create()));
            given(deviceJpaRepository.findById(DEVICE_ID))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    organizationBookmarkService.createOrganizationBookmark(ORGANIZATION_ID, request)
            ).isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 디바이스입니다.");
        }

        @Test
        void 예외_존재하지_않는_플레이스() {
            // given
            OrganizationBookmarkRequest request = new OrganizationBookmarkRequest(DEVICE_ID);

            given(organizationJpaRepository.findById(ORGANIZATION_ID))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    organizationBookmarkService.createOrganizationBookmark(ORGANIZATION_ID, request)
            ).isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 조직입니다.");
        }
    }

    @Nested
    class deleteOrganizationBookmark {

        @Test
        void 성공() {
            // given
            Device device = DeviceFixture.create(DEVICE_ID);
            OrganizationBookmarkRequest request = new OrganizationBookmarkRequest(DEVICE_ID);

            given(deviceJpaRepository.findById(DEVICE_ID))
                    .willReturn(Optional.of(device));

            // when
            organizationBookmarkService.deleteOrganizationBookmark(ORGANIZATION_ID, request);

            // then
            verify(organizationBookmarkJpaRepository).deleteByOrganizationIdAndDeviceId(ORGANIZATION_ID, DEVICE_ID);
            verify(notificationService).unsubscribeTopic(
                    device.getFcmToken(),
                    TopicConstants.getOrganizationTopicById(ORGANIZATION_ID)
            );
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            OrganizationBookmarkRequest request = new OrganizationBookmarkRequest(DEVICE_ID);

            given(deviceJpaRepository.findById(DEVICE_ID))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    organizationBookmarkService.deleteOrganizationBookmark(ORGANIZATION_ID, request)
            ).isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 디바이스입니다.");
        }
    }
}
