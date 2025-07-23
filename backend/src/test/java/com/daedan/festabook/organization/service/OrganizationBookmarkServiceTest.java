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
import com.daedan.festabook.organization.domain.OrganizationBookmark;
import com.daedan.festabook.organization.domain.OrganizationBookmarkFixture;
import com.daedan.festabook.organization.domain.OrganizationBookmarkRequestFixture;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.domain.OrganizationNotificationManager;
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
    private OrganizationNotificationManager organizationNotificationManager;

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
            Long organizationBookmarkId = 100L;
            OrganizationBookmark organizationBookmark = OrganizationBookmarkFixture.create(
                    organizationBookmarkId,
                    organization,
                    device
            );
            OrganizationBookmarkRequest request = OrganizationBookmarkRequestFixture.create(deviceId);

            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(organization));
            given(deviceJpaRepository.findById(deviceId))
                    .willReturn(Optional.of(device));
            given(organizationBookmarkJpaRepository.save(any()))
                    .willReturn(organizationBookmark);

            // when
            OrganizationBookmarkResponse result = organizationBookmarkService.createOrganizationBookmark(
                    organizationId, request);

            // then
            assertThat(result.id()).isEqualTo(organizationBookmarkId);
            then(organizationBookmarkJpaRepository).should()
                    .save(any());
            then(organizationNotificationManager).should()
                    .subscribeOrganizationTopic(any(), any());
        }

        @Test
        void 예외_조직에_이미_북마크한_디바이스() {
            // given
            Long organizationId = 1L;
            Long deviceId = 1L;

            OrganizationBookmarkRequest request = OrganizationBookmarkRequestFixture.create(deviceId);

            given(organizationBookmarkJpaRepository.existsByOrganizationIdAndDeviceId(organizationId, deviceId))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> organizationBookmarkService.createOrganizationBookmark(organizationId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미 북마크한 조직입니다.");
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            Long invalidDeviceId = 0L;
            OrganizationBookmarkRequest request = OrganizationBookmarkRequestFixture.create(invalidDeviceId);

            Long organizationId = 1L;
            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(OrganizationFixture.create()));
            given(deviceJpaRepository.findById(invalidDeviceId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    organizationBookmarkService.createOrganizationBookmark(organizationId, request)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 디바이스입니다.");
        }

        @Test
        void 예외_존재하지_않는_조직() {
            // given
            Long deviceId = 10L;
            OrganizationBookmarkRequest request = OrganizationBookmarkRequestFixture.create(deviceId);

            Long invalidOrganizationId = 0L;
            given(organizationJpaRepository.findById(invalidOrganizationId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    organizationBookmarkService.createOrganizationBookmark(invalidOrganizationId, request)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 조직입니다.");
        }
    }

    @Nested
    class deleteOrganizationBookmark {

        @Test
        void 성공() {
            // given
            Long organizationBookmarkId = 1L;
            Long deviceId = 10L;
            Long organizationId = 100L;

            Device device = DeviceFixture.create(deviceId);
            Organization organization = OrganizationFixture.create(organizationId);
            OrganizationBookmark organizationBookmark = OrganizationBookmarkFixture.create(
                    organizationBookmarkId,
                    organization,
                    device
            );

            given(organizationBookmarkJpaRepository.findById(organizationBookmarkId))
                    .willReturn(Optional.of(organizationBookmark));
            given(deviceJpaRepository.findById(deviceId))
                    .willReturn(Optional.of(device));

            // when
            organizationBookmarkService.deleteOrganizationBookmark(organizationBookmarkId);

            // then
            then(organizationBookmarkJpaRepository).should()
                    .deleteById(organizationBookmarkId);
            then(organizationNotificationManager).should()
                    .unsubscribeOrganizationTopic(organizationId, device.getFcmToken());
        }

        @Test
        void 성공_북마크_삭제시_조직_북마크가_존재하지_않아도_정상_처리() {
            // given
            Long invalidOrganizationBookmarkId = 0L;

            given(organizationBookmarkJpaRepository.findById(invalidOrganizationBookmarkId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatCode(() -> organizationBookmarkService.deleteOrganizationBookmark(invalidOrganizationBookmarkId))
                    .doesNotThrowAnyException();
            then(organizationNotificationManager)
                    .shouldHaveNoInteractions();
        }

        @Test
        void 성공_북마크_삭제시_디바이스가_존재하지_않아도_정상_처리() {
            // given
            Long organizationBookmarkId = 1L;
            Long invalidDeviceId = 0L;
            Long organizationId = 100L;

            Device device = DeviceFixture.create(invalidDeviceId);
            Organization organization = OrganizationFixture.create(organizationId);
            OrganizationBookmark organizationBookmark = OrganizationBookmarkFixture.create(
                    organizationBookmarkId,
                    organization,
                    device
            );

            given(organizationBookmarkJpaRepository.findById(organizationBookmarkId))
                    .willReturn(Optional.of(organizationBookmark));
            given(deviceJpaRepository.findById(invalidDeviceId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatCode(() -> organizationBookmarkService.deleteOrganizationBookmark(organizationBookmarkId))
                    .doesNotThrowAnyException();
            then(organizationNotificationManager)
                    .shouldHaveNoInteractions();
        }
    }
}
