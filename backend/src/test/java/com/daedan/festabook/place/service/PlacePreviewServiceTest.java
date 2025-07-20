package com.daedan.festabook.place.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceDetail;
import com.daedan.festabook.place.domain.PlaceDetailFixture;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.domain.PlaceImageFixture;
import com.daedan.festabook.place.dto.PlacePreviewResponses;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceDetailJpaRepository;
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
    private PlaceDetailJpaRepository placeDetailJpaRepository;

    @Mock
    private PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    @InjectMocks
    private PlacePreviewService placePreviewService;

    @Nested
    class getAllPreviewPlaceByOrganizationId {

        @Test
        void 성공() {
            // given
            Place place1 = PlaceFixture.create(1L);
            Place place2 = PlaceFixture.create(2L);
            List<Place> places = List.of(place1, place2);

            PlaceDetail placeDetail1 = PlaceDetailFixture.create(place1);
            PlaceDetail placeDetail2 = PlaceDetailFixture.create(place2);
            List<PlaceDetail> placeDetails = List.of(placeDetail1, placeDetail2);

            int representativeSequence = 1;
            PlaceImage placeImage1 = PlaceImageFixture.create(place1, representativeSequence);
            PlaceImage placeImage2 = PlaceImageFixture.create(place2, representativeSequence);
            List<PlaceImage> placeImages = List.of(placeImage1, placeImage2);

            Long organizationId = 1L;
            given(placeJpaRepository.findAllByOrganizationId(organizationId))
                    .willReturn(places);
            given(placeDetailJpaRepository.findAllByPlaceIn(places))
                    .willReturn(placeDetails);
            given(placeImageJpaRepository.findAllByPlaceInAndSequence(places, representativeSequence))
                    .willReturn(placeImages);

            int expectedSize = 2;

            // when
            PlacePreviewResponses result = placePreviewService.getAllPreviewPlaceByOrganizationId(organizationId);

            // then
            assertThat(result.responses()).hasSize(expectedSize);
        }

        @Test
        void 성공_대표_이미지가_없다면_null_반환() {
            // given
            Place place1 = PlaceFixture.create(1L);
            Place place2 = PlaceFixture.create(2L);
            List<Place> places = List.of(place1, place2);

            PlaceDetail placeDetail1 = PlaceDetailFixture.create(place1);
            PlaceDetail placeDetail2 = PlaceDetailFixture.create(place2);
            List<PlaceDetail> placeDetails = List.of(placeDetail1, placeDetail2);

            int representativeSequence = 1;
            PlaceImage placeImage1 = PlaceImageFixture.create(place1, representativeSequence);
            List<PlaceImage> placeImages = List.of(placeImage1);

            Long organizationId = 1L;

            given(placeJpaRepository.findAllByOrganizationId(organizationId))
                    .willReturn(places);
            given(placeDetailJpaRepository.findAllByPlaceIn(places))
                    .willReturn(placeDetails);
            given(placeImageJpaRepository.findAllByPlaceInAndSequence(places, representativeSequence))
                    .willReturn(placeImages);

            // when
            PlacePreviewResponses result = placePreviewService.getAllPreviewPlaceByOrganizationId(organizationId);

            // then
            assertSoftly(s -> {
                s.assertThat(result.responses().get(0).imageUrl()).isEqualTo(placeImage1.getImageUrl());
                s.assertThat(result.responses().get(1).imageUrl()).isEqualTo(null);
            });
        }
    }
}
