package com.daedan.festabook.place.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.dto.PlaceResponses;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
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
class PlaceServiceTest {

    @Mock
    private PlaceJpaRepository placeJpaRepository;

    @InjectMocks
    private PlaceService placeService;

    @Nested
    class findAllPlace {

        @Test
        void 성공() {
            // given
            String title = "코딩하며 한잔";
            String description = "시원한 맥주와 맛있는 치킨!";
            PlaceCategory category = PlaceCategory.BAR;
            String location = "공학관 앞";
            String host = "C블C블";
            LocalTime startTime = LocalTime.of(9, 0);
            LocalTime endTime = LocalTime.of(18, 0);

            Place place1 = new Place(title, description, category, location, host, startTime, endTime);
            Place place2 = new Place(title, description, category, location, host, startTime, endTime);
            Place place3 = new Place(title, description, category, location, host, startTime, endTime);
            given(placeJpaRepository.findAll())
                    .willReturn(List.of(place1, place2, place3));

            // when
            PlaceResponses result = placeService.findAllPlace();

            // then
            assertSoftly(s -> {
                s.assertThat(result.responses()).hasSize(3);
                s.assertThat(result.responses().get(0).id()).isEqualTo(place1.getId());
                s.assertThat(result.responses().get(1).id()).isEqualTo(place2.getId());
                s.assertThat(result.responses().get(2).id()).isEqualTo(place3.getId());
            });
        }
    }
}
