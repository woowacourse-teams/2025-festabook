package com.daedan.festabook.place.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalTime;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlaceDetailTest {

    @Nested
    class update {

        @Test
        void 성공() {
            // given
            PlaceDetail placeDetail = PlaceDetailFixture.create();

            PlaceDetail newPlaceDetail = new PlaceDetail(
                    PlaceFixture.create(),
                    "업데이트할 이름",
                    "업데이트할 설명",
                    "업데이트할 위치",
                    "업데이트할 호스트",
                    LocalTime.of(12, 30),
                    LocalTime.of(13, 30)
            );

            // when
            placeDetail.update(newPlaceDetail);

            // then
            assertSoftly(s -> {
                s.assertThat(placeDetail.getTitle()).isEqualTo(newPlaceDetail.getTitle());
                s.assertThat(placeDetail.getDescription()).isEqualTo(newPlaceDetail.getDescription());
                s.assertThat(placeDetail.getLocation()).isEqualTo(newPlaceDetail.getLocation());
                s.assertThat(placeDetail.getHost()).isEqualTo(newPlaceDetail.getHost());
                s.assertThat(placeDetail.getStartTime()).isEqualTo(newPlaceDetail.getStartTime());
                s.assertThat(placeDetail.getEndTime()).isEqualTo(newPlaceDetail.getEndTime());
            });
        }

        @Test
        void 성공_플레이스는_변경되지_않음() {
            // given
            PlaceDetail placeDetail = PlaceDetailFixture.create();

            PlaceDetail newPlaceDetail = new PlaceDetail(
                    PlaceFixture.create(),
                    "업데이트할 이름",
                    "업데이트할 설명",
                    "업데이트할 위치",
                    "업데이트할 호스트",
                    LocalTime.of(12, 30),
                    LocalTime.of(13, 30)
            );

            // when
            placeDetail.update(newPlaceDetail);

            // then
            assertThat(placeDetail.getPlace()).isNotEqualTo(newPlaceDetail.getPlace());
        }
    }
}
