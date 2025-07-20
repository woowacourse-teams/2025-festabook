package com.daedan.festabook.place.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceAnnouncementFixture;
import com.daedan.festabook.place.domain.PlaceDetail;
import com.daedan.festabook.place.domain.PlaceDetailFixture;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.domain.PlaceImageFixture;
import com.daedan.festabook.place.dto.PlaceResponse;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceDetailJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
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
    private PlaceImageJpaRepository placeImageJpaRepository;

    @Mock
    private PlaceDetailJpaRepository placeDetailJpaRepository;

    @Mock
    private PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    @InjectMocks
    private PlaceService placeService;

    @Nested
    class getPlaceByPlaceId {

        @Test
        void 성공() {
            // given
            Long placeId = 1L;

            Place place = PlaceFixture.create(placeId);

            PlaceDetail detail = PlaceDetailFixture.create(place);

            PlaceImage image1 = PlaceImageFixture.create(place);
            PlaceImage image2 = PlaceImageFixture.create(place);

            PlaceAnnouncement announcement1 = PlaceAnnouncementFixture.create(place);
            PlaceAnnouncement announcement2 = PlaceAnnouncementFixture.create(place);

            given(placeDetailJpaRepository.findById(placeId))
                    .willReturn(Optional.of(detail));
            given(placeImageJpaRepository.findAllByPlaceIdOrderBySequenceAsc(placeId))
                    .willReturn(List.of(image1, image2));
            given(placeAnnouncementJpaRepository.findAllByPlaceId(placeId))
                    .willReturn(List.of(announcement1, announcement2));

            // when
            PlaceResponse result = placeService.getPlaceByPlaceId(placeId);

            // then
            assertSoftly(s -> {
                s.assertThat(result).isNotNull();
                s.assertThat(result.placeImages().responses()).hasSize(2);
                s.assertThat(result.placeAnnouncements().responses()).hasSize(2);
            });
        }

        @Test
        void 성공_이미지_sequence로_오름차순_정렬() {
            // given
            Long placeId = 1L;

            Place place = PlaceFixture.create(placeId);

            PlaceDetail detail = PlaceDetailFixture.create(place);

            PlaceImage image3 = PlaceImageFixture.create(place, 3);
            PlaceImage image2 = PlaceImageFixture.create(place, 2);
            PlaceImage image1 = PlaceImageFixture.create(place, 1);

            given(placeDetailJpaRepository.findById(placeId))
                    .willReturn(Optional.of(detail));
            given(placeImageJpaRepository.findAllByPlaceIdOrderBySequenceAsc(placeId))
                    .willReturn(List.of(image1, image2, image3));

            // when
            PlaceResponse result = placeService.getPlaceByPlaceId(placeId);

            // then
            assertSoftly(s -> {
                s.assertThat(result.placeImages().responses().get(0).sequence()).isEqualTo(image1.getSequence());
                s.assertThat(result.placeImages().responses().get(1).sequence()).isEqualTo(image2.getSequence());
                s.assertThat(result.placeImages().responses().get(2).sequence()).isEqualTo(image3.getSequence());
            });
        }

        @Test
        void 실패_존재하지_않는_place_id() {
            // given
            Long placeId = 999L;

            // when & then
            // TODO: ExceptionHandler 등록 후 변경
            assertThatThrownBy(() -> placeService.getPlaceByPlaceId(placeId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 플레이스 세부 정보입니다.");
        }
    }
}
