package com.daedan.festabook.place.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceAnnouncementFixture;
import com.daedan.festabook.place.dto.PlaceAnnouncementUpdateRequest;
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

    @Mock
    private PlaceJpaRepository placeJpaRepository;

    @Mock
    private PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    @InjectMocks
    private PlaceAnnouncementService placeAnnouncementService;

    @Nested
    class updatePlaceAnnouncement {

        @Test
        void 성공() {
            // given
            Long placeAnnouncementId = 1L;
            PlaceAnnouncement placeAnnouncement = PlaceAnnouncementFixture.create("제목", "내용");

            given(placeAnnouncementJpaRepository.findById(placeAnnouncementId))
                    .willReturn(Optional.of(placeAnnouncement));

            PlaceAnnouncementUpdateRequest request = new PlaceAnnouncementUpdateRequest("수정된 제목", "수정된 내용");

            // when
            PlaceAnnouncementUpdateResponse result = placeAnnouncementService.updatePlaceAnnouncement(
                    placeAnnouncementId, request);

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

            given(placeAnnouncementJpaRepository.findById(placeAnnouncementId))
                    .willReturn(Optional.empty());

            PlaceAnnouncementUpdateRequest request = new PlaceAnnouncementUpdateRequest("수정된 제목", "수정된 내용");

            // when & then
            assertThatThrownBy(() -> placeAnnouncementService.updatePlaceAnnouncement(placeAnnouncementId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 플레이스 공지입니다.");
        }
    }
}
