package com.daedan.festabook.place.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.domain.PlaceImageFixture;
import com.daedan.festabook.place.dto.PlaceEtcPreviewResponses;
import com.daedan.festabook.place.dto.PlaceMainPreviewResponses;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
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
class PlacePreviewServiceTest {

    @Mock
    private PlaceJpaRepository placeJpaRepository;

    @Mock
    private PlaceImageJpaRepository placeImageJpaRepository;

    @InjectMocks
    private PlacePreviewService placePreviewService;

    @Nested
    class getAllPreviewMainPlaceByFestivalId {

        @Test
        void 성공() {
            // given
            Place place1 = PlaceFixture.create(1L);
            Place place2 = PlaceFixture.create(2L);
            List<Place> places = List.of(place1, place2);

            int representativeSequence = 1;
            PlaceImage placeImage1 = PlaceImageFixture.create(place1, representativeSequence);
            PlaceImage placeImage2 = PlaceImageFixture.create(place2, representativeSequence);
            List<PlaceImage> placeImages = List.of(placeImage1, placeImage2);

            Long festivalId = 1L;
            given(placeJpaRepository.findAllByFestivalId(festivalId))
                    .willReturn(places);
            given(placeImageJpaRepository.findAllByPlaceInAndSequence(places, representativeSequence))
                    .willReturn(placeImages);

            int expectedSize = 2;

            // when
            PlaceMainPreviewResponses result = placePreviewService.getAllPreviewMainPlaceByFestivalId(festivalId);

            // then
            assertThat(result.responses()).hasSize(expectedSize);
        }

        @Test
        void 성공_대표_이미지가_없다면_null_반환() {
            // given
            Place place1 = PlaceFixture.create(1L);
            Place place2 = PlaceFixture.create(2L);
            List<Place> places = List.of(place1, place2);

            int representativeSequence = 1;
            PlaceImage placeImage1 = PlaceImageFixture.create(place1, representativeSequence);
            List<PlaceImage> placeImages = List.of(placeImage1);

            Long festivalId = 1L;

            given(placeJpaRepository.findAllByFestivalId(festivalId))
                    .willReturn(places);
            given(placeImageJpaRepository.findAllByPlaceInAndSequence(places, representativeSequence))
                    .willReturn(placeImages);

            // when
            PlaceMainPreviewResponses result = placePreviewService.getAllPreviewMainPlaceByFestivalId(festivalId);

            // then
            assertSoftly(s -> {
                s.assertThat(result.responses().get(0).imageUrl()).isEqualTo(placeImage1.getImageUrl());
                s.assertThat(result.responses().get(1).imageUrl()).isEqualTo(null);
            });
        }
    }

    @Nested
    class getAllPreviewEtcPlaceByFestivalId {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);

            PlaceCategory etcPlaceCategory = PlaceCategory.SMOKING;
            String expectedTitle = "기타 플레이스입니다.";

            Place etcPlace = PlaceFixture.createWithNullDefaults(festival, etcPlaceCategory, expectedTitle);

            given(placeJpaRepository.findAllByFestivalId(festivalId))
                    .willReturn(List.of(etcPlace));

            // when
            PlaceEtcPreviewResponses result = placePreviewService.getAllPreviewEtcPlaceByFestivalId(festivalId);

            // then
            assertThat(result.responses().get(0).title()).isEqualTo(expectedTitle);
        }

        @Test
        void 성공_기타_플레이스만_조회() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);

            PlaceCategory etcPlaceCategory = PlaceCategory.SMOKING;
            String expectedTitle = "기타 플레이스입니다.";

            Place etcPlace = PlaceFixture.createWithNullDefaults(festival, etcPlaceCategory, expectedTitle);

            PlaceCategory mainPlaceCategory = PlaceCategory.BAR;
            Place mainPlace1 = PlaceFixture.create(festival, mainPlaceCategory);
            Place mainPlace2 = PlaceFixture.create(festival, mainPlaceCategory);

            given(placeJpaRepository.findAllByFestivalId(festivalId))
                    .willReturn(List.of(etcPlace, mainPlace1, mainPlace2));

            int expectedSize = 1;

            // when
            PlaceEtcPreviewResponses result = placePreviewService.getAllPreviewEtcPlaceByFestivalId(festivalId);

            // then
            assertThat(result.responses()).hasSize(expectedSize);
        }
    }
}
