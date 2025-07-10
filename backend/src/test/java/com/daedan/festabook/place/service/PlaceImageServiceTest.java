package com.daedan.festabook.place.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.dto.PlaceImageResponses;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
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
class PlaceImageServiceTest {

    @Mock
    private PlaceImageJpaRepository placeImageJpaRepository;

    @InjectMocks
    private PlaceImageService placeImageService;

    @Nested
    class findAllPlaceImageByPlaceId {

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
            String imageUrl = "https://~";

            PlaceImage placeImage1 = new PlaceImage(place, imageUrl);
            PlaceImage placeImage2 = new PlaceImage(place, imageUrl);
            PlaceImage placeImage3 = new PlaceImage(place, imageUrl);
            given(placeImageJpaRepository.findAllByPlaceId(id))
                    .willReturn(List.of(placeImage1, placeImage2, placeImage3));

            // when
            PlaceImageResponses result = placeImageService.findAllPlaceImageByPlaceId(id);

            // then
            assertSoftly(s -> {
                s.assertThat(result.responses()).hasSize(3);
                s.assertThat(result.responses().get(0).id()).isEqualTo(placeImage1.getId());
                s.assertThat(result.responses().get(1).id()).isEqualTo(placeImage2.getId());
                s.assertThat(result.responses().get(2).id()).isEqualTo(placeImage3.getId());
            });
        }
    }
}
