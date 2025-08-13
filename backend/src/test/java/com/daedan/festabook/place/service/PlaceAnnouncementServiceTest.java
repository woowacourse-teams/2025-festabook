package com.daedan.festabook.place.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.dto.PlaceAnnouncementRequest;
import com.daedan.festabook.place.dto.PlaceAnnouncementResponse;
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
            Place place = PlaceFixture.create();

            String title = "공지입니다.";
            String content = "공지 내용입니다.";

            PlaceAnnouncementRequest request = new PlaceAnnouncementRequest(title, content);

            PlaceAnnouncement placeAnnouncement = new PlaceAnnouncement(place, title, content);

            given(placeJpaRepository.findById(place.getId()))
                    .willReturn(Optional.of(place));
            given(placeAnnouncementJpaRepository.countByPlace(place))
                    .willReturn(0);
            given(placeAnnouncementJpaRepository.save(any()))
                    .willReturn(placeAnnouncement);

            // when
            PlaceAnnouncementResponse result = placeAnnouncementService.createPlaceAnnouncement(place.getId(), request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.title()).isEqualTo(title);
                s.assertThat(result.content()).isEqualTo(content);
            });
        }

        @Test
        void 예외_공지_최대_갯수_초과() {
            // given
            Place place = PlaceFixture.create();

            String title = "공지입니다.";
            String content = "공지 내용입니다.";

            PlaceAnnouncementRequest request = new PlaceAnnouncementRequest(title, content);

            given(placeJpaRepository.findById(place.getId()))
                    .willReturn(Optional.of(place));
            given(placeAnnouncementJpaRepository.countByPlace(place))
                    .willReturn(PLACE_ANNOUNCEMENT_MAX_COUNT);

            // when & then
            assertThatThrownBy(() -> placeAnnouncementService.createPlaceAnnouncement(place.getId(), request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(String.format("플레이스 공지사항은 %d개까지 작성이 가능합니다.", PLACE_ANNOUNCEMENT_MAX_COUNT));
        }
    }
}
