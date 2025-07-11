package com.daedan.festabook.place.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.domain.PlaceImageFixture;
import com.daedan.festabook.place.dto.PlaceImageResponses;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
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
    class getAllPlaceImageByPlaceId {

        @Test
        void 성공() {
            // given
            Long id = 1L;
            PlaceImage placeImage1 = PlaceImageFixture.create();
            PlaceImage placeImage2 = PlaceImageFixture.create();
            PlaceImage placeImage3 = PlaceImageFixture.create();

            given(placeImageJpaRepository.findAllByPlaceId(id))
                    .willReturn(List.of(placeImage1, placeImage2, placeImage3));

            // when
            PlaceImageResponses result = placeImageService.getAllPlaceImageByPlaceId(id);

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
