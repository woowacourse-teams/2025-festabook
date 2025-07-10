package com.daedan.festabook.place.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.dto.PlaceAnnouncementResponses;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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

    @Mock
    private PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    @InjectMocks
    private PlaceAnnouncementService placeAnnouncementService;

    @Nested
    class findAllPlaceAnnouncementByPlaceId {

        @Test
        void 성공() {
            // given
            Long id = 1L;
            Place place = new Place(
                    "코딩하며 한잔",
                    "시원한 맥주와 맛있는 치킨!",
                    PlaceCategory.BAR,
                    "공학관 앞",
                    "C블C블",
                    LocalTime.of(9, 0),
                    LocalTime.of(18, 0)
            );
            String title = "치킨 재고 소진되었습니다.";
            String content = "앞으로 더 좋은 주점으로 찾아뵙겠습니다.";
            LocalDate date = LocalDate.of(2025, 7, 9);
            LocalTime time = LocalTime.of(15, 0);

            PlaceAnnouncement placeAnnouncement1 = new PlaceAnnouncement(place, title, content, date, time);
            PlaceAnnouncement placeAnnouncement2 = new PlaceAnnouncement(place, title, content, date, time);
            PlaceAnnouncement placeAnnouncement3 = new PlaceAnnouncement(place, title, content, date, time);
            given(placeAnnouncementJpaRepository.findAllByPlaceId(id))
                    .willReturn(List.of(placeAnnouncement1, placeAnnouncement2, placeAnnouncement3));

            // when
            PlaceAnnouncementResponses result = placeAnnouncementService.findAllPlaceAnnouncementByPlaceId(id);

            // then
            assertSoftly(s -> {
                        s.assertThat(result.responses()).hasSize(3);
                        s.assertThat(result.responses().get(0).id()).isEqualTo(placeAnnouncement1.getId());
                        s.assertThat(result.responses().get(0).id()).isEqualTo(placeAnnouncement1.getId());
                        s.assertThat(result.responses().get(0).id()).isEqualTo(placeAnnouncement1.getId());
                    }
            );
        }
    }
}
