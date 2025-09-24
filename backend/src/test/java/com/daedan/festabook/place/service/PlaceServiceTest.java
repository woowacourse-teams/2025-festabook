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
import com.daedan.festabook.place.dto.EtcPlaceUpdateRequest;
import com.daedan.festabook.place.dto.EtcPlaceUpdateRequestFixture;
import com.daedan.festabook.place.dto.EtcPlaceUpdateResponse;
import com.daedan.festabook.place.dto.MainPlaceUpdateRequest;
import com.daedan.festabook.place.dto.MainPlaceUpdateRequestFixture;
import com.daedan.festabook.place.dto.MainPlaceUpdateResponse;
import com.daedan.festabook.place.dto.PlaceBulkCloneRequest;
import com.daedan.festabook.place.dto.PlaceBulkCloneRequestFixture;
import com.daedan.festabook.place.dto.PlaceBulkCloneResponse;
import com.daedan.festabook.place.dto.PlaceCreateRequest;
import com.daedan.festabook.place.dto.PlaceCreateRequestFixture;
import com.daedan.festabook.place.dto.PlaceCreateResponse;
import com.daedan.festabook.place.dto.PlaceResponse;
import com.daedan.festabook.place.dto.PlaceResponses;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceFavoriteJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import com.daedan.festabook.timetag.domain.PlaceTimeTag;
import com.daedan.festabook.timetag.domain.PlaceTimeTagFixture;
import com.daedan.festabook.timetag.domain.TimeTag;
import com.daedan.festabook.timetag.domain.TimeTagFixture;
import com.daedan.festabook.timetag.infrastructure.PlaceTimeTagJpaRepository;
import com.daedan.festabook.timetag.infrastructure.TimeTagJpaRepository;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;
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
    private PlaceTimeTagJpaRepository placeTimeTagJpaRepository;

    @Mock
    private TimeTagJpaRepository timeTagJpaRepository;

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
            PlaceCreateRequest placeRequest = PlaceCreateRequestFixture.create(expectedPlaceCategory,
                    expectedPlaceTitle);

            Festival festival = FestivalFixture.create(festivalId);
            Place place = PlaceFixture.createWithNullDefaults(festival, expectedPlaceCategory, expectedPlaceTitle,
                    expectedPlaceId);

            given(festivalJpaRepository.findById(festivalId))
                    .willReturn(Optional.of(festival));
            given(placeJpaRepository.save(any()))
                    .willReturn(place);

            // when
            PlaceCreateResponse response = placeService.createPlace(festivalId, placeRequest);

            // then
            assertSoftly(s -> {
                s.assertThat(response.placeId()).isEqualTo(expectedPlaceId);
                s.assertThat(response.category()).isEqualTo(expectedPlaceCategory);
                s.assertThat(response.title()).isEqualTo(expectedPlaceTitle);
            });
        }

        @Test
        void 예외_존재하지_않는_축제() {
            // given
            Long invalidFestivalId = 0L;

            PlaceCreateRequest placeRequest = PlaceCreateRequestFixture.create();

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
    class bulkClonePlaces {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            given(festivalJpaRepository.findById(festivalId))
                    .willReturn(Optional.of(festival));

            // 기존 플레이스 조회
            Place place1 = PlaceFixture.create(festival, 1L);
            Place place2 = PlaceFixture.create(festival, 2L);
            Place place3 = PlaceFixture.create(festival, 3L);
            List<Long> originalPlaceIds = List.of(place1.getId(), place2.getId(), place3.getId());
            List<Place> originalPlaces = List.of(place1, place2, place3);
            given(placeJpaRepository.findAllByIdInAndFestivalId(originalPlaceIds, festivalId))
                    .willReturn(originalPlaces);

            given(placeImageJpaRepository.findAllByPlace(any()))
                    .willReturn(List.of());

            // 복제한 플레이스 저장
            Place clonedPlace1 = PlaceFixture.create();
            Place clonedPlace2 = PlaceFixture.create();
            Place clonedPlace3 = PlaceFixture.create();
            List<Place> clonedPlaces = List.of(clonedPlace1, clonedPlace2, clonedPlace3);
            List<Place> savedClonePlaces = List.of(
                    PlaceFixture.create(4L),
                    PlaceFixture.create(5L),
                    PlaceFixture.create(6L)
            );
            given(placeJpaRepository.saveAll(clonedPlaces))
                    .willReturn(savedClonePlaces);

            PlaceBulkCloneRequest request = new PlaceBulkCloneRequest(
                    List.of(place1.getId(), place2.getId(), place3.getId()));

            List<Long> expected = savedClonePlaces.stream().map(Place::getId).toList();

            // when
            PlaceBulkCloneResponse result = placeService.bulkClonePlaces(festivalId, request);

            // then
            assertThat(result.clonedPlaceIds()).containsExactly(expected.toArray(new Long[]{}));
        }

        @Test
        void 성공_이미지도_같이_복제() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            given(festivalJpaRepository.findById(festivalId))
                    .willReturn(Optional.of(festival));

            // 기존 플레이스 조회
            Place place1 = PlaceFixture.create(festival, 1L);
            List<Long> originalPlaceIds = List.of(place1.getId());
            List<Place> originalPlaces = List.of(place1);
            given(placeJpaRepository.findAllByIdInAndFestivalId(originalPlaceIds, festivalId))
                    .willReturn(originalPlaces);

            PlaceImageFixture.create(place1);
            given(placeImageJpaRepository.findAllByPlace(any()))
                    .willReturn(List.of());

            // 복제한 플레이스 저장
            Place clonedPlace1 = PlaceFixture.create();
            List<Place> clonedPlaces = List.of(clonedPlace1);
            List<Place> savedClonePlaces = List.of(PlaceFixture.create(4L));
            given(placeJpaRepository.saveAll(clonedPlaces))
                    .willReturn(savedClonePlaces);

            PlaceBulkCloneRequest request = new PlaceBulkCloneRequest(List.of(place1.getId()));

            // when
            placeService.bulkClonePlaces(festivalId, request);

            // then
            then(placeImageJpaRepository).should().saveAll(any());
        }

        @Test
        void 예외_festival에_속하지_않은_플레이스_복제() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            given(festivalJpaRepository.findById(festivalId))
                    .willReturn(Optional.of(festival));

            Long anotherFestivalId = 2L;
            Festival anotherFestival = FestivalFixture.create(anotherFestivalId);

            // 기존 플레이스 조회
            Place place = PlaceFixture.create(festival, 1L);
            Place anotherFestivalPlace = PlaceFixture.create(anotherFestival, 2L);
            List<Long> originalPlaceIds = List.of(place.getId(), anotherFestivalPlace.getId());
            List<Place> originalPlaces = List.of(place);
            given(placeJpaRepository.findAllByIdInAndFestivalId(originalPlaceIds, festivalId))
                    .willReturn(originalPlaces);

            PlaceBulkCloneRequest request = new PlaceBulkCloneRequest(
                    List.of(place.getId(), anotherFestivalPlace.getId()));

            // when & then
            assertThatThrownBy(() -> placeService.bulkClonePlaces(festivalId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("플레이스의 접근 권한이 없습니다.");
        }

        @Test
        void 예외_존재하지_않는_축제() {
            // given
            Long invalidFestivalId = 1L;
            given(festivalJpaRepository.findById(invalidFestivalId))
                    .willReturn(Optional.empty());

            PlaceBulkCloneRequest request = PlaceBulkCloneRequestFixture.create();

            // when & then
            assertThatThrownBy(() -> placeService.bulkClonePlaces(invalidFestivalId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 축제입니다.");
        }

        @Test
        void 예외_클론_갯수_최대() {
            // given
            Long festivalId = 1L;
            List<Long> originalPlaceIds = LongStream.rangeClosed(0, 200)
                    .boxed()
                    .toList();

            PlaceBulkCloneRequest request = PlaceBulkCloneRequestFixture.create(originalPlaceIds);

            // when & then
            assertThatThrownBy(() -> placeService.bulkClonePlaces(festivalId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("한 번에 복제할 수 있는 사이즈가 초과하였습니다.");
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
    class updateMainPlace {

        @Test
        void 성공_place_필드_값() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);

            Long placeId = 1L;
            Place place = PlaceFixture.create(festival, placeId);

            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(place));

            MainPlaceUpdateRequest request = MainPlaceUpdateRequestFixture.create(
                    PlaceCategory.BAR,
                    "수정된 플레이스 이름",
                    "수정된 플레이스 설명",
                    "수정된 위치",
                    "수정된 호스트",
                    LocalTime.of(12, 00),
                    LocalTime.of(13, 00)
            );

            // when
            MainPlaceUpdateResponse result = placeService.updateMainPlace(festivalId, placeId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.placeCategory()).isEqualTo(request.placeCategory());
                s.assertThat(result.title()).isEqualTo(request.title());
                s.assertThat(result.description()).isEqualTo(request.description());
                s.assertThat(result.location()).isEqualTo(request.location());
                s.assertThat(result.host()).isEqualTo(request.host());
                s.assertThat(result.startTime()).isEqualTo(request.startTime());
                s.assertThat(result.endTime()).isEqualTo(request.endTime());
                s.assertThat(result.timeTags()).isEmpty();
            });
        }

        @Test
        void 성공_시간_태그_수정() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);

            Long placeId = 1L;
            Place place = PlaceFixture.create(festival, placeId);

            Long originalTimeTagId1 = 1L;
            TimeTag originalTimeTag1 = TimeTagFixture.createWithFestivalAndId(festival, originalTimeTagId1);
            PlaceTimeTag placeTimeTag1 = PlaceTimeTagFixture.createWithPlaceAndTimeTag(place, originalTimeTag1);

            Long originalAndUpdateTimeTagId2 = 2L;
            TimeTag originalAndUpdateTimeTag2 = TimeTagFixture.createWithFestivalAndId(
                    festival,
                    originalAndUpdateTimeTagId2
            );
            PlaceTimeTag placeTimeTag2 = PlaceTimeTagFixture.createWithPlaceAndTimeTag(
                    place,
                    originalAndUpdateTimeTag2
            );

            Long updateTimeTagId3 = 3L;
            TimeTag updateTimeTag3 = TimeTagFixture.createWithFestivalAndId(festival, updateTimeTagId3);
            PlaceTimeTag placeTimeTag3 = PlaceTimeTagFixture.createWithPlaceAndTimeTag(place, updateTimeTag3);

            List<PlaceTimeTag> originalPlaceTimeTags = List.of(placeTimeTag1, placeTimeTag2);
            List<Long> updateTimeTagIds = List.of(originalAndUpdateTimeTagId2, updateTimeTagId3);

            // 2는 기존 존재하므로 추가 항목에서 제외.
            List<TimeTag> updateTimeTags = List.of(updateTimeTag3);

            // 첫 번째는 기존 존재하는 시간 태그 조회
            // 두 번째는 최종 저장된 PlaceTimeTag 조회
            given(placeTimeTagJpaRepository.findAllByPlaceId(placeId))
                    .willReturn(originalPlaceTimeTags, List.of(placeTimeTag2, placeTimeTag3));

            // 추가할 시간 태그 조회
            given(timeTagJpaRepository.findAllById(any()))
                    .willReturn(updateTimeTags);

            // place 조회
            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(place));

            MainPlaceUpdateRequest request = MainPlaceUpdateRequestFixture.createWithFullFiled(
                    PlaceCategory.BAR,
                    "수정된 플레이스 이름",
                    "수정된 플레이스 설명",
                    "수정된 위치",
                    "수정된 호스트",
                    LocalTime.of(12, 00),
                    LocalTime.of(13, 00),
                    updateTimeTagIds
            );

            // when
            MainPlaceUpdateResponse result = placeService.updateMainPlace(festivalId, placeId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.placeCategory()).isEqualTo(request.placeCategory());
                s.assertThat(result.title()).isEqualTo(request.title());
                s.assertThat(result.description()).isEqualTo(request.description());
                s.assertThat(result.location()).isEqualTo(request.location());
                s.assertThat(result.host()).isEqualTo(request.host());
                s.assertThat(result.startTime()).isEqualTo(request.startTime());
                s.assertThat(result.endTime()).isEqualTo(request.endTime());
                s.assertThat(result.timeTags()).containsAll(updateTimeTagIds);
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

            MainPlaceUpdateRequest request = MainPlaceUpdateRequestFixture.create();

            // when & then
            assertThatThrownBy(() -> placeService.updateMainPlace(otherFestival.getId(), place.getId(), request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 플레이스가 아닙니다.");
        }

        @Test
        void 예외_다른_축제_시간_태그() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);

            Long otherFestivalId = 999L;
            Festival otherFestival = FestivalFixture.create(otherFestivalId);

            Long placeId = 1L;
            Place place = PlaceFixture.create(festival, placeId);

            Long otherTimeTagId = 1L;
            TimeTag otherTimeTag = TimeTagFixture.createWithFestivalAndId(otherFestival, otherTimeTagId);

            List<Long> updateTimeTagIds = List.of(otherTimeTagId);
            List<TimeTag> updateTimeTags = List.of(otherTimeTag);

            // place 조회
            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(place));

            // 추가할 시간 태그 조회
            given(timeTagJpaRepository.findAllById(any()))
                    .willReturn(updateTimeTags);

            MainPlaceUpdateRequest request = MainPlaceUpdateRequestFixture.createWithFullFiled(
                    PlaceCategory.BAR,
                    "수정된 플레이스 이름",
                    "수정된 플레이스 설명",
                    "수정된 위치",
                    "수정된 호스트",
                    LocalTime.of(12, 00),
                    LocalTime.of(13, 00),
                    updateTimeTagIds
            );

            // when & then
            assertThatThrownBy(() -> placeService.updateMainPlace(festivalId, placeId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 시간 태그가 아닙니다.");
        }
    }

    @Nested
    class updateEtcPlace {

        @Test
        void 성공() {
            // given
            Long placeId = 1L;
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Place place = PlaceFixture.create(festival, placeId);

            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(place));

            EtcPlaceUpdateRequest request = EtcPlaceUpdateRequestFixture.create("수정된 플레이스 이름");

            // when
            EtcPlaceUpdateResponse result = placeService.updateEtcPlace(festivalId, placeId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.title()).isEqualTo(request.title());
                s.assertThat(result.timeTags()).isEmpty();
            });
        }

        @Test
        void 성공_time_tag_수정() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);

            Long placeId = 1L;
            Place place = PlaceFixture.createWithNullDefaults(festival, placeId);

            Long originalTimeTagId1 = 1L;
            TimeTag originalTimeTag1 = TimeTagFixture.createWithFestivalAndId(festival, originalTimeTagId1);
            PlaceTimeTag placeTimeTag1 = PlaceTimeTagFixture.createWithPlaceAndTimeTag(place, originalTimeTag1);

            Long originalAndUpdateTimeTagId2 = 2L;
            TimeTag originalAndUpdateTimeTag2 = TimeTagFixture.createWithFestivalAndId(
                    festival,
                    originalAndUpdateTimeTagId2
            );
            PlaceTimeTag placeTimeTag2 = PlaceTimeTagFixture.createWithPlaceAndTimeTag(
                    place,
                    originalAndUpdateTimeTag2
            );

            Long updateTimeTagId3 = 3L;
            TimeTag updateTimeTag3 = TimeTagFixture.createWithFestivalAndId(festival, updateTimeTagId3);
            PlaceTimeTag placeTimeTag3 = PlaceTimeTagFixture.createWithPlaceAndTimeTag(place, updateTimeTag3);

            List<PlaceTimeTag> originalPlaceTimeTags = List.of(placeTimeTag1, placeTimeTag2);
            List<Long> updateTimeTagIds = List.of(originalAndUpdateTimeTagId2, updateTimeTagId3);

            // 2는 기존 존재하므로 추가 항목에서 제외.
            List<TimeTag> updateTimeTags = List.of(updateTimeTag3);

            // 첫 번째는 기존 존재하는 시간 태그 조회
            // 두 번째는 최종 저장된 PlaceTimeTag 조회
            given(placeTimeTagJpaRepository.findAllByPlaceId(placeId))
                    .willReturn(originalPlaceTimeTags, List.of(placeTimeTag2, placeTimeTag3));

            // 추가할 시간 태그 조회
            given(timeTagJpaRepository.findAllById(any()))
                    .willReturn(updateTimeTags);

            // place 조회
            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(place));

            EtcPlaceUpdateRequest request = EtcPlaceUpdateRequestFixture.create("수정된 플레이스 이름", updateTimeTagIds);

            // when
            EtcPlaceUpdateResponse result = placeService.updateEtcPlace(festivalId, placeId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.title()).isEqualTo(request.title());
                s.assertThat(result.timeTags()).containsAll(updateTimeTagIds);
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

            EtcPlaceUpdateRequest request = EtcPlaceUpdateRequestFixture.create();

            // when & then
            assertThatThrownBy(() -> placeService.updateEtcPlace(otherFestival.getId(), place.getId(), request))
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
            Place place = PlaceFixture.create(festival, placeId);

            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(place));

            // when
            placeService.deleteByPlaceId(festivalId, placeId);

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
            assertThatThrownBy(() -> placeService.deleteByPlaceId(otherFestival.getId(), place.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 플레이스가 아닙니다.");
        }
    }
}
