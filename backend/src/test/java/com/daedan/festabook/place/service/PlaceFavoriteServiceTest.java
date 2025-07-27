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
import com.daedan.festabook.place.domain.PlaceFavorite;
import com.daedan.festabook.place.domain.PlaceFavoriteFixture;
import com.daedan.festabook.place.domain.PlaceFavoriteRequestFixture;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.dto.PlaceFavoriteRequest;
import com.daedan.festabook.place.dto.PlaceFavoriteResponse;
import com.daedan.festabook.place.infrastructure.PlaceFavoriteJpaRepository;
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
class PlaceFavoriteServiceTest {

    @Mock
    private PlaceFavoriteJpaRepository placeFavoriteJpaRepository;

    @Mock
    private DeviceJpaRepository deviceJpaRepository;

    @Mock
    private PlaceJpaRepository placeJpaRepository;

    @InjectMocks
    private PlaceFavoriteService placeFavoriteService;

    @Nested
    class createPlaceFavorite {

        @Test
        void 성공() {
            // given
            Long placeId = 1L;
            Place place = PlaceFixture.create(placeId);
            Long deviceId = 10L;
            Device device = DeviceFixture.create(deviceId);
            Long placeFavoriteId = 100L;
            PlaceFavorite placeFavorite = PlaceFavoriteFixture.create(placeFavoriteId, place, device);
            PlaceFavoriteRequest request = new PlaceFavoriteRequest(deviceId);

            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            given(deviceJpaRepository.findById(deviceId))
                    .willReturn(Optional.of(device));
            given(placeFavoriteJpaRepository.save(any(PlaceFavorite.class)))
                    .willReturn(placeFavorite);

            // when
            PlaceFavoriteResponse result = placeFavoriteService.createPlaceFavorite(placeId, request);

            // then
            assertThat(result.id()).isEqualTo(placeFavoriteId);
            then(placeFavoriteJpaRepository).should()
                    .save(any());
        }

        @Test
        void 예외_플레이스에_이미_즐겨찾기한_디바이스() {
            // given
            Long placeId = 1L;
            Long deviceId = 1L;

            PlaceFavoriteRequest request = PlaceFavoriteRequestFixture.create(deviceId);

            given(placeFavoriteJpaRepository.existsByPlaceIdAndDeviceId(placeId, deviceId))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> placeFavoriteService.createPlaceFavorite(placeId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미 즐겨찾기한 플레이스입니다.");
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            Long invalidDeviceId = 0L;
            PlaceFavoriteRequest request = PlaceFavoriteRequestFixture.create(invalidDeviceId);

            Long placeId = 1L;
            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(PlaceFixture.create()));
            given(deviceJpaRepository.findById(invalidDeviceId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> placeFavoriteService.createPlaceFavorite(placeId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 디바이스입니다.");
        }

        @Test
        void 예외_존재하지_않는_플레이스() {
            // given
            Long deviceId = 10L;
            PlaceFavoriteRequest request = PlaceFavoriteRequestFixture.create(deviceId);

            Long invalidPlaceId = 0L;
            given(placeJpaRepository.findById(invalidPlaceId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> placeFavoriteService.createPlaceFavorite(invalidPlaceId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 플레이스입니다.");
        }
    }

    @Nested
    class deletePlaceFavorite {

        @Test
        void 성공() {
            // given
            Long placeFavoriteId = 1L;
            Long deviceId = 10L;
            Long placeId = 100L;

            Device device = DeviceFixture.create(deviceId);
            Place place = PlaceFixture.create(placeId);
            PlaceFavorite placeFavorite = PlaceFavoriteFixture.create(placeFavoriteId, place, device);

            given(placeFavoriteJpaRepository.findById(placeFavoriteId))
                    .willReturn(Optional.of(placeFavorite));
            given(deviceJpaRepository.findById(deviceId))
                    .willReturn(Optional.of(device));

            // when
            placeFavoriteService.deletePlaceFavorite(placeFavoriteId);

            // then
            then(placeFavoriteJpaRepository).should()
                    .deleteById(placeFavoriteId);
        }

        @Test
        void 성공_즐겨찾기_삭제시_플레이스_즐겨찾기가_존재하지_않아도_정상_처리() {
            // given
            Long invalidPlaceFavoriteId = 0L;

            given(placeFavoriteJpaRepository.findById(invalidPlaceFavoriteId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatCode(() -> placeFavoriteService.deletePlaceFavorite(invalidPlaceFavoriteId))
                    .doesNotThrowAnyException();
        }

        @Test
        void 성공_즐겨찾기_삭제시_디바이스가_존재하지_않아도_정상_처리() {
            // given
            Long placeFavoriteId = 1L;
            Long invalidDeviceId = 0L;
            Long placeId = 100L;

            Device device = DeviceFixture.create(invalidDeviceId);
            Place place = PlaceFixture.create(placeId);
            PlaceFavorite placeFavorite = PlaceFavoriteFixture.create(placeFavoriteId, place, device);

            given(placeFavoriteJpaRepository.findById(placeFavoriteId))
                    .willReturn(Optional.of(placeFavorite));
            given(deviceJpaRepository.findById(invalidDeviceId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatCode(() -> placeFavoriteService.deletePlaceFavorite(placeFavoriteId))
                    .doesNotThrowAnyException();
        }
    }
}
