package com.daedan.festabook.place.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceBookmark;
import com.daedan.festabook.place.domain.PlaceBookmarkFixture;
import com.daedan.festabook.place.domain.PlaceBookmarkRequestFixture;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.domain.PlaceNotificationManager;
import com.daedan.festabook.place.dto.PlaceBookmarkRequest;
import com.daedan.festabook.place.dto.PlaceBookmarkResponse;
import com.daedan.festabook.place.infrastructure.PlaceBookmarkJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
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
class PlaceBookmarkServiceTest {

    @Mock
    private PlaceBookmarkJpaRepository placeBookmarkJpaRepository;

    @Mock
    private DeviceJpaRepository deviceJpaRepository;

    @Mock
    private PlaceJpaRepository placeJpaRepository;

    @Mock
    private PlaceNotificationManager placeNotificationManager;

    @InjectMocks
    private PlaceBookmarkService placeBookmarkService;

    @Nested
    class createPlaceBookmark {

        @Test
        void 성공() {
            // given
            Long placeId = 1L;
            Place place = PlaceFixture.create(placeId);
            Long deviceId = 10L;
            Device device = DeviceFixture.create(deviceId);
            Long placeBookmarkId = 100L;
            PlaceBookmark placeBookmark = PlaceBookmarkFixture.create(placeBookmarkId, place, device);
            PlaceBookmarkRequest request = new PlaceBookmarkRequest(deviceId);

            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            given(deviceJpaRepository.findById(deviceId))
                    .willReturn(Optional.of(device));
            given(placeBookmarkJpaRepository.save(any(PlaceBookmark.class)))
                    .willReturn(placeBookmark);

            // when
            PlaceBookmarkResponse result = placeBookmarkService.createPlaceBookmark(placeId, request);

            // then
            assertThat(result.id()).isEqualTo(placeBookmarkId);
            then(placeBookmarkJpaRepository).should()
                    .save(any());
            then(placeNotificationManager).should()
                    .subscribePlaceTopic(any(), any());
        }

        @Test
        void 예외_이미_북마크한_플레이스() {
            // given
            Long placeId = 1L;
            Long deviceId = 1L;

            PlaceBookmarkRequest request = PlaceBookmarkRequestFixture.create(deviceId);

            given(placeBookmarkJpaRepository.existsByPlaceIdAndDeviceId(placeId, deviceId))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> placeBookmarkService.createPlaceBookmark(placeId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미 북마크한 플레이스입니다.");
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            Long invalidDeviceId = 0L;
            PlaceBookmarkRequest request = PlaceBookmarkRequestFixture.create(invalidDeviceId);

            Long placeId = 1L;
            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(PlaceFixture.create()));
            given(deviceJpaRepository.findById(invalidDeviceId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> placeBookmarkService.createPlaceBookmark(placeId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 디바이스입니다.");
        }

        @Test
        void 예외_존재하지_않는_플레이스() {
            // given
            Long deviceId = 10L;
            PlaceBookmarkRequest request = PlaceBookmarkRequestFixture.create(deviceId);

            Long invalidPlaceId = 0L;
            given(placeJpaRepository.findById(invalidPlaceId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> placeBookmarkService.createPlaceBookmark(invalidPlaceId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 플레이스입니다.");
        }
    }

    @Nested
    class deletePlaceBookmark {

        @Test
        void 성공() {
            // given
            Long placeBookmarkId = 1L;
            Long deviceId = 10L;
            Long placeId = 100L;

            Device device = DeviceFixture.create(deviceId);
            Place place = PlaceFixture.create(placeId);
            PlaceBookmark placeBookmark = PlaceBookmarkFixture.create(placeBookmarkId, place, device);

            given(placeBookmarkJpaRepository.findById(placeBookmarkId))
                    .willReturn(Optional.of(placeBookmark));
            given(deviceJpaRepository.findById(deviceId))
                    .willReturn(Optional.of(device));

            // when
            placeBookmarkService.deletePlaceBookmark(placeBookmarkId);

            // then
            then(placeBookmarkJpaRepository).should()
                    .deleteById(placeBookmarkId);
            then(placeNotificationManager).should()
                    .unsubscribePlaceTopic(any(), any());
        }

        @Test
        void 성공_북마크_삭제시_플레이스_북마크가_존재하지_않아도_정상_처리() {
            // given
            Long invalidPlaceBookmarkId = 0L;

            given(placeBookmarkJpaRepository.findById(invalidPlaceBookmarkId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatCode(() -> placeBookmarkService.deletePlaceBookmark(invalidPlaceBookmarkId))
                    .doesNotThrowAnyException();
            then(placeNotificationManager)
                    .shouldHaveNoInteractions();
        }

        @Test
        void 성공_북마크_삭제시_디바이스가_존재하지_않아도_정상_처리() {
            // given
            Long placeBookmarkId = 1L;
            Long invalidDeviceId = 0L;
            Long placeId = 100L;

            Device device = DeviceFixture.create(invalidDeviceId);
            Place place = PlaceFixture.create(placeId);
            PlaceBookmark placeBookmark = PlaceBookmarkFixture.create(placeBookmarkId, place, device);

            given(placeBookmarkJpaRepository.findById(placeBookmarkId))
                    .willReturn(Optional.of(placeBookmark));
            given(deviceJpaRepository.findById(invalidDeviceId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatCode(() -> placeBookmarkService.deletePlaceBookmark(placeBookmarkId))
                    .doesNotThrowAnyException();
            then(placeNotificationManager)
                    .shouldHaveNoInteractions();
        }
    }
}
