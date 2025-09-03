package com.daedan.festabook.place.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceAnnouncementFixture;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.dto.PlaceAnnouncementRequest;
import com.daedan.festabook.place.dto.PlaceAnnouncementRequestFixture;
import com.daedan.festabook.place.dto.PlaceAnnouncementResponse;
import com.daedan.festabook.place.dto.PlaceAnnouncementUpdateRequest;
import com.daedan.festabook.place.dto.PlaceAnnouncementUpdateRequestFixture;
import com.daedan.festabook.place.dto.PlaceAnnouncementUpdateResponse;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
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
class PlaceAnnouncementServiceTest {

    private static final int PLACE_ANNOUNCEMENT_MAX_COUNT = 3;

    @Mock
    private PlaceJpaRepository placeJpaRepository;

    @Mock
    private PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    @InjectMocks
    private PlaceAnnouncementService placeAnnouncementService;

    @Nested
    class createPlaceAnnouncement {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Place place = PlaceFixture.create(festival);

            PlaceAnnouncementRequest request = PlaceAnnouncementRequestFixture.create();

            PlaceAnnouncement placeAnnouncement = new PlaceAnnouncement(place, request.title(), request.content());

            given(placeJpaRepository.findById(place.getId()))
                    .willReturn(Optional.of(place));
            given(placeAnnouncementJpaRepository.countByPlace(place))
                    .willReturn(0);
            given(placeAnnouncementJpaRepository.save(any()))
                    .willReturn(placeAnnouncement);

            // when
            PlaceAnnouncementResponse result = placeAnnouncementService.createPlaceAnnouncement(
                    festival.getId(),
                    place.getId(),
                    request
            );

            // then
            assertSoftly(s -> {
                s.assertThat(result.title()).isEqualTo(request.title());
                s.assertThat(result.content()).isEqualTo(request.content());
            });
        }

        @Test
        void 예외_공지_최대_개수_초과() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Place place = PlaceFixture.create(festival);

            given(placeJpaRepository.findById(place.getId()))
                    .willReturn(Optional.of(place));
            given(placeAnnouncementJpaRepository.countByPlace(place))
                    .willReturn(PLACE_ANNOUNCEMENT_MAX_COUNT);

            PlaceAnnouncementRequest request = PlaceAnnouncementRequestFixture.create();

            // when & then
            assertThatThrownBy(() ->
                    placeAnnouncementService.createPlaceAnnouncement(festival.getId(), place.getId(), request)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(String.format("플레이스 공지사항은 %d개까지 작성이 가능합니다.", PLACE_ANNOUNCEMENT_MAX_COUNT));
        }

        @Test
        void 예외_다른_축제의_플레이스일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Long placeId = 1L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            Place place = PlaceFixture.create(placeId, requestFestival);

            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(place));

            PlaceAnnouncementRequest request = PlaceAnnouncementRequestFixture.create();

            // when & then
            assertThatThrownBy(() ->
                    placeAnnouncementService.createPlaceAnnouncement(
                            otherFestival.getId(),
                            place.getId(),
                            request
                    )
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 플레이스가 아닙니다.");
        }
    }

    @Nested
    class updatePlaceAnnouncement {

        @Test
        void 성공() {
            // given
            Long placeAnnouncementId = 1L;
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Place place = PlaceFixture.create(festival);
            PlaceAnnouncement placeAnnouncement = PlaceAnnouncementFixture.create(place, "제목", "내용");

            given(placeAnnouncementJpaRepository.findById(placeAnnouncementId))
                    .willReturn(Optional.of(placeAnnouncement));

            PlaceAnnouncementUpdateRequest request = PlaceAnnouncementUpdateRequestFixture.create(
                    "수정된 제목",
                    "수정된 내용"
            );

            // when
            PlaceAnnouncementUpdateResponse result = placeAnnouncementService.updatePlaceAnnouncement(
                    festivalId,
                    placeAnnouncementId,
                    request
            );

            // then
            assertSoftly(s -> {
                s.assertThat(result.title()).isEqualTo(request.title());
                s.assertThat(result.content()).isEqualTo(request.content());
            });
        }

        @Test
        void 예외_플레이스_공지사항이_존재하지_않음() {
            // given
            Long placeAnnouncementId = 1L;
            Long festivalId = 1L;

            given(placeAnnouncementJpaRepository.findById(placeAnnouncementId))
                    .willReturn(Optional.empty());

            PlaceAnnouncementUpdateRequest request = PlaceAnnouncementUpdateRequestFixture.create(
                    "수정된 제목",
                    "수정된 내용"
            );

            // when & then
            assertThatThrownBy(() ->
                    placeAnnouncementService.updatePlaceAnnouncement(festivalId, placeAnnouncementId, request)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 플레이스 공지입니다.");
        }

        @Test
        void 예외_다른_축제의_플레이스_공지일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Long placeAnnouncementId = 1L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            Place place = PlaceFixture.create(requestFestival);
            PlaceAnnouncement placeAnnouncement = PlaceAnnouncementFixture.create(placeAnnouncementId, place);

            given(placeAnnouncementJpaRepository.findById(placeAnnouncementId))
                    .willReturn(Optional.of(placeAnnouncement));

            PlaceAnnouncementUpdateRequest request = PlaceAnnouncementUpdateRequestFixture.create();

            // when & then
            assertThatThrownBy(() ->
                    placeAnnouncementService.updatePlaceAnnouncement(
                            otherFestival.getId(),
                            placeAnnouncement.getId(),
                            request
                    )
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 플레이스 공지가 아닙니다.");
        }
    }

    @Nested
    class deleteByPlaceAnnouncementId {

        @Test
        void 성공() {
            // given
            Long placeAnnouncementId = 1L;
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Place place = PlaceFixture.create(festival);
            PlaceAnnouncement placeAnnouncement = PlaceAnnouncementFixture.create(placeAnnouncementId, place);

            given(placeAnnouncementJpaRepository.findById(placeAnnouncementId))
                    .willReturn(Optional.of(placeAnnouncement));

            // when
            placeAnnouncementService.deleteByPlaceAnnouncementId(festivalId, placeAnnouncementId);

            // then
            then(placeAnnouncementJpaRepository).should()
                    .deleteById(placeAnnouncementId);
        }

        @Test
        void 예외_다른_축제의_플레이스_공지일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Long placeAnnouncementId = 1L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            Place place = PlaceFixture.create(requestFestival);
            PlaceAnnouncement placeAnnouncement = PlaceAnnouncementFixture.create(placeAnnouncementId, place);

            given(placeAnnouncementJpaRepository.findById(placeAnnouncement.getId()))
                    .willReturn(Optional.of(placeAnnouncement));

            // when & then
            assertThatThrownBy(() ->
                    placeAnnouncementService.deleteByPlaceAnnouncementId(
                            otherFestival.getId(),
                            placeAnnouncement.getId()
                    )
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 플레이스 공지가 아닙니다.");
        }
    }
}
