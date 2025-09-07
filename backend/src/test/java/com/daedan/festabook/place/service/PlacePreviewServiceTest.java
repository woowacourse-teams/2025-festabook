package com.daedan.festabook.place.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.global.infrastructure.ShuffleManager;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.domain.PlaceImageFixture;
import com.daedan.festabook.place.dto.PlacePreviewResponses;
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

    @Mock
    private ShuffleManager shuffleManager;

    @InjectMocks
    private PlacePreviewService placePreviewService;

    @Nested
    class getAllPreviewPlaceByFestivalIdSortByRandom {

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
            List<Place> shuffledPlaces = List.of(place2, place1);
            given(shuffleManager.getShuffledList(places))
                    .willReturn(shuffledPlaces);
            given(placeImageJpaRepository.findAllByPlaceInAndSequence(shuffledPlaces, representativeSequence))
                    .willReturn(placeImages);

            int expectedSize = 2;

            // when
            PlacePreviewResponses result = placePreviewService.getAllPreviewPlaceByFestivalIdSortByRandom(festivalId);

            // then
            assertThat(result.responses()).hasSize(expectedSize);
            then(shuffleManager).should()
                    .getShuffledList(places);
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
            List<Place> shuffledPlaces = List.of(place2, place1);
            given(shuffleManager.getShuffledList(places))
                    .willReturn(shuffledPlaces);
            given(placeImageJpaRepository.findAllByPlaceInAndSequence(shuffledPlaces, representativeSequence))
                    .willReturn(placeImages);

            // when
            PlacePreviewResponses result = placePreviewService.getAllPreviewPlaceByFestivalIdSortByRandom(festivalId);

            // then
            assertSoftly(s -> {
                s.assertThat(result.responses().get(0).imageUrl()).isEqualTo(null);
                s.assertThat(result.responses().get(1).imageUrl()).isEqualTo(placeImage1.getImageUrl());
            });
            then(shuffleManager).should()
                    .getShuffledList(places);
        }
    }
}
