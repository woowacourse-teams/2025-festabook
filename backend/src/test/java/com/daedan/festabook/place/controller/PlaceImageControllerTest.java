package com.daedan.festabook.place.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.domain.PlaceImageFixture;
import com.daedan.festabook.place.dto.PlaceImageSequenceUpdateRequest;
import com.daedan.festabook.place.dto.PlaceImageSequenceUpdateRequestFixture;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PlaceImageControllerTest {

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Autowired
    private PlaceJpaRepository placeJpaRepository;

    @Autowired
    private PlaceImageJpaRepository placeImageJpaRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class updateFestivalImagesSequence {

        @Test
        void 성공_수정_후_응답값_오름차순_정렬() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Place place = PlaceFixture.create(festival);
            placeJpaRepository.save(place);

            PlaceImage placeImage1 = PlaceImageFixture.create(place, 1);
            PlaceImage placeImage2 = PlaceImageFixture.create(place, 2);
            PlaceImage placeImage3 = PlaceImageFixture.create(place, 3);
            placeImageJpaRepository.saveAll(List.of(placeImage1, placeImage2, placeImage3));

            List<PlaceImageSequenceUpdateRequest> requests = List.of(
                    PlaceImageSequenceUpdateRequestFixture.create(placeImage1.getId(), 3),
                    PlaceImageSequenceUpdateRequestFixture.create(placeImage2.getId(), 2),
                    PlaceImageSequenceUpdateRequestFixture.create(placeImage3.getId(), 1)
            );

            int expectedSize = 3;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(requests)
                    .when()
                    .patch("/places/images/sequences")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))

                    .body("[0].placeImageId", equalTo(placeImage3.getId().intValue()))
                    .body("[0].sequence", equalTo(1))

                    .body("[1].placeImageId", equalTo(placeImage2.getId().intValue()))
                    .body("[1].sequence", equalTo(2))

                    .body("[2].placeImageId", equalTo(placeImage1.getId().intValue()))
                    .body("[2].sequence", equalTo(3));
        }
    }
}
