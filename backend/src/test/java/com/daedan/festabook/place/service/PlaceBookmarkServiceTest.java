package com.daedan.festabook.place.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceBookmark;
import com.daedan.festabook.place.domain.PlaceFixture;
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
    private NotificationService notificationService;

    @InjectMocks
    private PlaceBookmarkService placeBookmarkService;

    private static final Long PLACE_ID = 1L;
    private static final Long DEVICE_ID = 10L;

    @Nested
    class createPlaceBookmark {

        @Test
        void 성공() {
            // given
            Place place = PlaceFixture.create();
            Device device = DeviceFixture.create(DEVICE_ID);
            PlaceBookmarkRequest request = new PlaceBookmarkRequest(DEVICE_ID);

            given(placeJpaRepository.findById(PLACE_ID))
                    .willReturn(Optional.of(place));
            given(deviceJpaRepository.findById(DEVICE_ID))
                    .willReturn(Optional.of(device));

            // when
            PlaceBookmarkResponse result = placeBookmarkService.createPlaceBookmark(PLACE_ID, request);

            // then
            assertThat(result).isNotNull();
            verify(placeBookmarkJpaRepository).save(any(PlaceBookmark.class));
            verify(notificationService).subscribeTopic(device.getFcmToken(),
                    TopicConstants.getPlaceTopicById(PLACE_ID));
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            PlaceBookmarkRequest request = new PlaceBookmarkRequest(DEVICE_ID);

            given(placeJpaRepository.findById(PLACE_ID))
                    .willReturn(Optional.of(PlaceFixture.create()));
            given(deviceJpaRepository.findById(DEVICE_ID))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    placeBookmarkService.createPlaceBookmark(PLACE_ID, request)
            ).isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 디바이스입니다.");
        }

        @Test
        void 예외_존재하지_않는_플레이스() {
            // given
            PlaceBookmarkRequest request = new PlaceBookmarkRequest(DEVICE_ID);

            given(placeJpaRepository.findById(PLACE_ID))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    placeBookmarkService.createPlaceBookmark(PLACE_ID, request)
            ).isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 플레이스입니다.");
        }
    }

    @Nested
    class deletePlaceBookmark {

        @Test
        void 성공() {
            // given
            Device device = DeviceFixture.create(DEVICE_ID);
            PlaceBookmarkRequest request = new PlaceBookmarkRequest(DEVICE_ID);

            given(deviceJpaRepository.findById(DEVICE_ID))
                    .willReturn(Optional.of(device));

            // when
            placeBookmarkService.deletePlaceBookmark(PLACE_ID, request);

            // then
            verify(placeBookmarkJpaRepository).deleteByPlaceIdAndDeviceId(PLACE_ID, DEVICE_ID);
            verify(notificationService).unsubscribeTopic(
                    device.getFcmToken(),
                    TopicConstants.getPlaceTopicById(PLACE_ID)
            );
        }

        @Test
        void 예외_존재하지_않는_디바이스() {
            // given
            PlaceBookmarkRequest request = new PlaceBookmarkRequest(DEVICE_ID);

            given(deviceJpaRepository.findById(DEVICE_ID))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    placeBookmarkService.deletePlaceBookmark(PLACE_ID, request)
            ).isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 디바이스입니다.");
        }
    }
}
