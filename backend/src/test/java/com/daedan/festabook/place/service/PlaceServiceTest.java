package com.daedan.festabook.place.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.dto.PlaceResponses;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PlaceServiceTest {

    private final PlaceJpaRepository placeJpaRepository;
    private final PlaceService placeService;

    public PlaceServiceTest() {
        this.placeJpaRepository = mock(PlaceJpaRepository.class);
        this.placeService = new PlaceService(placeJpaRepository);
    }

    @Nested
    class 플레이스_조회 {

        @Test
        void 플레이스를_정상적으로_전체_조회한다() {
            // given
            String title = "제목";
            String description = "설명";
            PlaceCategory category = PlaceCategory.BOOTH;
            String location = "장소";
            String host = "주최";
            LocalTime startTime = LocalTime.of(9, 0);
            LocalTime endTime = LocalTime.of(18, 0);

            Place place1 = new Place(1L, title, description, category, location, host, startTime, endTime);
            Place place2 = new Place(2L, title, description, category, location, host, startTime, endTime);
            Place place3 = new Place(3L, title, description, category, location, host, startTime, endTime);
            given(placeJpaRepository.findAll())
                    .willReturn(List.of(place1, place2, place3));

            // when
            PlaceResponses actual = placeService.findAllPlace();

            // then
            assertThat(actual.responses()).hasSize(3);
            assertThat(actual.responses().get(0).id()).isEqualTo(place1.getId());
            assertThat(actual.responses().get(1).id()).isEqualTo(place2.getId());
            assertThat(actual.responses().get(2).id()).isEqualTo(place3.getId());
        }
    }
}
