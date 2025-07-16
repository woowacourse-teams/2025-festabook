package com.daedan.festabook.place.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceAnnouncementFixture;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.domain.PlaceImageFixture;
import com.daedan.festabook.place.dto.PlaceListResponses;
import com.daedan.festabook.place.dto.PlaceResponse;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlaceServiceTest {

    @Mock
    private PlaceJpaRepository placeJpaRepository;

    @Mock
    private PlaceImageJpaRepository placeImageJpaRepository;

    @Mock
    private PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    @InjectMocks
    private PlaceService placeService;

    @Nested
    class getAllPlaceByOrganizationId {

        @Test
        void 성공() {
            // given
            Long organizationId = 1L;

            Place place1 = PlaceFixture.create();
            Place place2 = PlaceFixture.create();
            Place place3 = PlaceFixture.create();
            ReflectionTestUtils.setField(place1, "id", 1L);
            ReflectionTestUtils.setField(place2, "id", 2L);
            ReflectionTestUtils.setField(place3, "id", 3L);

            PlaceImage placeImage1 = PlaceImageFixture.create(place1);
            PlaceImage placeImage2 = PlaceImageFixture.create(place2);
            PlaceImage placeImage3 = PlaceImageFixture.create(place3);

            given(placeJpaRepository.findAllByOrganizationId(organizationId))
                    .willReturn(List.of(place1, place2, place3));
            given(placeImageJpaRepository.findAllByPlaceIdIn(List.of(place1.getId(), place2.getId(), place3.getId())))
                    .willReturn(List.of(placeImage1, placeImage2, placeImage3));

            // when
            PlaceListResponses result = placeService.getAllPlaceByOrganizationId(organizationId);

            // then
            assertThat(result.responses()).hasSize(3);
        }
    }

    @Nested
    class getPlaceById {

        @Test
        void 성공() {
            // given
            Long placeId = 1L;

            Place place = PlaceFixture.create();
            ReflectionTestUtils.setField(place, "id", placeId);

            PlaceImage image1 = PlaceImageFixture.create(place);
            PlaceImage image2 = PlaceImageFixture.create(place);

            PlaceAnnouncement announcement1 = PlaceAnnouncementFixture.create(place);
            PlaceAnnouncement announcement2 = PlaceAnnouncementFixture.create(place);

            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            given(placeImageJpaRepository.findAllByPlaceId(placeId))
                    .willReturn(List.of(image1, image2));
            given(placeAnnouncementJpaRepository.findAllByPlaceId(placeId))
                    .willReturn(List.of(announcement1, announcement2));

            // when
            PlaceResponse result = placeService.getPlaceById(placeId);

            // then
            assertSoftly(s -> {
                s.assertThat(result).isNotNull();
                s.assertThat(result.placeImages().responses()).hasSize(2);
                s.assertThat(result.placeImages().responses()).hasSize(2);
            });
        }

        @Test
        void 실패_존재하지_않는_place_id() {
            // given
            Long placeId = 999L;

            // when & then
            assertThatThrownBy(() -> placeService.getPlaceById(placeId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 플레이스입니다.");
        }
    }
}
