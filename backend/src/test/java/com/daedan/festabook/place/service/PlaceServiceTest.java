package com.daedan.festabook.place.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.dto.PlaceResponses;
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
class PlaceServiceTest {

    @Mock
    private PlaceJpaRepository placeJpaRepository;

    @InjectMocks
    private PlaceService placeService;

    @Nested
    class getAllPlace {

        @Test
        void 성공() {
            // given
            Place place1 = PlaceFixture.create();
            Place place2 = PlaceFixture.create();
            Place place3 = PlaceFixture.create();

            given(placeJpaRepository.findAll())
                    .willReturn(List.of(place1, place2, place3));

            // when
            PlaceResponses result = placeService.getAllPlace();

            // then
            assertSoftly(s -> {
                s.assertThat(result.responses()).hasSize(3);
                s.assertThat(result.responses().get(0).title()).isEqualTo(place1.getTitle());
                s.assertThat(result.responses().get(1).title()).isEqualTo(place2.getTitle());
                s.assertThat(result.responses().get(2).title()).isEqualTo(place3.getTitle());
            });
        }
    }
}
