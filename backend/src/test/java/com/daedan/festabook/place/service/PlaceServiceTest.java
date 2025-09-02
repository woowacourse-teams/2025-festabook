package com.daedan.festabook.place.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceAnnouncementFixture;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.domain.PlaceImageFixture;
import com.daedan.festabook.place.dto.PlaceRequest;
import com.daedan.festabook.place.dto.PlaceRequestFixture;
import com.daedan.festabook.place.dto.PlaceResponse;
import com.daedan.festabook.place.dto.PlaceResponses;
import com.daedan.festabook.place.dto.PlaceUpdateRequest;
import com.daedan.festabook.place.dto.PlaceUpdateRequestFixture;
import com.daedan.festabook.place.dto.PlaceUpdateResponse;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceFavoriteJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import java.time.LocalTime;
import java.util.List;
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
class PlaceServiceTest {

    @Mock
    private PlaceJpaRepository placeJpaRepository;

    @Mock
    private PlaceImageJpaRepository placeImageJpaRepository;

    @Mock
    private FestivalJpaRepository festivalJpaRepository;

    @Mock
    private PlaceFavoriteJpaRepository placeFavoriteJpaRepository;

    @Mock
    private PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    @InjectMocks
    private PlaceService placeService;

    @Nested
    class createPlace {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Long expectedPlaceId = 1L;
            PlaceCategory expectedPlaceCategory = PlaceCategory.BAR;
            String expectedPlaceTitle = "남문 주차장";
            PlaceRequest placeRequest = PlaceRequestFixture.create(expectedPlaceCategory, expectedPlaceTitle);

            Festival festival = FestivalFixture.create(festivalId);
            Place place = PlaceFixture.createWithNullDefaults(expectedPlaceId, festival, expectedPlaceCategory,
                    expectedPlaceTitle);

            given(festivalJpaRepository.findById(festivalId))
                    .willReturn(Optional.of(festival));
            given(placeJpaRepository.save(any()))
                    .willReturn(place);

            // when
            PlaceResponse result = placeService.createPlace(festivalId, placeRequest);

            // then
            assertSoftly(s -> {
                s.assertThat(result.placeId()).isEqualTo(expectedPlaceId);
                s.assertThat(result.category()).isEqualTo(expectedPlaceCategory);
                s.assertThat(result.title()).isEqualTo(expectedPlaceTitle);

                s.assertThat(result.placeImages().responses()).isEmpty();
                s.assertThat(result.placeAnnouncements().responses()).isEmpty();

                s.assertThat(result.startTime()).isNull();
                s.assertThat(result.endTime()).isNull();
                s.assertThat(result.location()).isNull();
                s.assertThat(result.host()).isNull();
                s.assertThat(result.description()).isNull();
            });
        }

        @Test
        void 예외_존재하지_않는_축제() {
            // given
            Long invalidFestivalId = 0L;

            PlaceRequest placeRequest = PlaceRequestFixture.create();

            given(festivalJpaRepository.findById(invalidFestivalId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> placeService.createPlace(invalidFestivalId, placeRequest))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 축제입니다.");

            then(placeJpaRepository).should(never())
                    .save(any());
        }
    }

    @Nested
    class getAllPlaceByFestivalId {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;

            Festival festival = FestivalFixture.create(festivalId);
            Place place = PlaceFixture.create(festival);

            PlaceImage image = PlaceImageFixture.create(place);
            PlaceAnnouncement announcement = PlaceAnnouncementFixture.create(place);

            given(placeJpaRepository.findAllByFestivalId(festivalId))
                    .willReturn(List.of(place));
            given(placeImageJpaRepository.findAllByPlaceIdOrderBySequenceAsc(place.getId()))
                    .willReturn(List.of(image));
            given(placeAnnouncementJpaRepository.findAllByPlaceId(place.getId()))
                    .willReturn(List.of(announcement));

            // when
            PlaceResponses result = placeService.getAllPlaceByFestivalId(festivalId);

            // then
            assertThat(result.responses()).hasSize(1);
        }
    }

    @Nested
    class getPlaceByPlaceId {

        @Test
        void 성공() {
            // given
            Long placeId = 1L;
            Place place = PlaceFixture.create(placeId);

            PlaceImage image1 = PlaceImageFixture.create(place);
            PlaceImage image2 = PlaceImageFixture.create(place);

            PlaceAnnouncement announcement1 = PlaceAnnouncementFixture.create(place);
            PlaceAnnouncement announcement2 = PlaceAnnouncementFixture.create(place);

            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            given(placeImageJpaRepository.findAllByPlaceIdOrderBySequenceAsc(placeId))
                    .willReturn(List.of(image1, image2));
            given(placeAnnouncementJpaRepository.findAllByPlaceId(placeId))
                    .willReturn(List.of(announcement1, announcement2));

            // when
            PlaceResponse result = placeService.getPlaceByPlaceId(placeId);

            // then
            assertSoftly(s -> {
                s.assertThat(result.placeId()).isEqualTo(placeId);
                s.assertThat(result.category()).isEqualTo(place.getCategory());

                s.assertThat(result.description()).isEqualTo(place.getDescription());
                s.assertThat(result.host()).isEqualTo(place.getHost());
                s.assertThat(result.location()).isEqualTo(place.getLocation());
                s.assertThat(result.endTime()).isEqualTo(place.getEndTime());
                s.assertThat(result.startTime()).isEqualTo(place.getStartTime());
                s.assertThat(result.title()).isEqualTo(place.getTitle());

                s.assertThat(result.placeImages().responses()).hasSize(2);
                s.assertThat(result.placeAnnouncements().responses()).hasSize(2);
            });
        }

        @Test
        void 예외_존재하지_않는_place_id() {
            // given
            Long inValidPlaceId = 0L;

            // when & then
            assertThatThrownBy(() -> placeService.getPlaceByPlaceId(inValidPlaceId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 플레이스입니다.");
        }
    }

    @Nested
    class updatePlace {

        @Test
        void 성공() {
            // given
            Long placeId = 1L;
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Place place = PlaceFixture.create(placeId, festival);

            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(place));

            PlaceUpdateRequest request = PlaceUpdateRequestFixture.create(
                    PlaceCategory.BAR,
                    "수정된 플레이스 이름",
                    "수정된 플레이스 설명",
                    "수정된 위치",
                    "수정된 호스트",
                    LocalTime.of(12, 00),
                    LocalTime.of(13, 00)
            );

            // when
            PlaceUpdateResponse result = placeService.updatePlace(placeId, festivalId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.placeCategory()).isEqualTo(request.placeCategory());
                s.assertThat(result.title()).isEqualTo(request.title());
                s.assertThat(result.description()).isEqualTo(request.description());
                s.assertThat(result.location()).isEqualTo(request.location());
                s.assertThat(result.host()).isEqualTo(request.host());
                s.assertThat(result.startTime()).isEqualTo(request.startTime());
                s.assertThat(result.endTime()).isEqualTo(request.endTime());
            });
        }

        @Test
        void 예외_다른_축제의_플레이스일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            Place place = PlaceFixture.create(requestFestival);

            given(placeJpaRepository.findById(place.getId()))
                    .willReturn(Optional.of(place));

            PlaceUpdateRequest request = PlaceUpdateRequestFixture.create();

            // when & then
            assertThatThrownBy(() -> placeService.updatePlace(place.getId(), otherFestival.getId(), request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 플레이스가 아닙니다.");
        }
    }

    @Nested
    class deleteByPlaceId {

        @Test
        void 성공() {
            // given
            Long placeId = 1L;
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Place place = PlaceFixture.create(placeId, festival);

            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(place));

            // when
            placeService.deleteByPlaceId(placeId, festivalId);

            // then
            then(placeImageJpaRepository).should()
                    .deleteAllByPlaceId(placeId);
            then(placeAnnouncementJpaRepository).should()
                    .deleteAllByPlaceId(placeId);
            then(placeFavoriteJpaRepository).should()
                    .deleteAllByPlaceId(placeId);
            then(placeJpaRepository).should()
                    .deleteById(placeId);
        }

        @Test
        void 예외_다른_축제의_플레이스일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            Place place = PlaceFixture.create(requestFestival);

            given(placeJpaRepository.findById(place.getId()))
                    .willReturn(Optional.of(place));

            // when & then
            assertThatThrownBy(() -> placeService.deleteByPlaceId(place.getId(), otherFestival.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 플레이스가 아닙니다.");
        }
    }
}
