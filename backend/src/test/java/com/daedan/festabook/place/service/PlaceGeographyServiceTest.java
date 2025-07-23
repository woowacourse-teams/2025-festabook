package com.daedan.festabook.place.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.dto.PlaceGeographyResponses;
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
class PlaceGeographyServiceTest {

    @Mock
    private PlaceJpaRepository placeJpaRepository;

    @InjectMocks
    private PlaceGeographyService placeGeographyService;

    @Nested
    class getAllPlaceGeographyByOrganizationId {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create(1L);

            Place place1 = PlaceFixture.create(organization, PlaceCategory.BAR, 125.432, 37.123);
            Place place2 = PlaceFixture.create(organization, PlaceCategory.SMOKING, 125.782, 37.343);

            given(placeJpaRepository.findAllByOrganizationId(organization.getId()))
                    .willReturn(List.of(place1, place2));

            // when
            PlaceGeographyResponses result = placeGeographyService.getAllPlaceGeographyByOrganizationId(
                    organization.getId()
            );

            // then
            assertSoftly(s -> {
                s.assertThat(result.responses()).hasSize(2);
                s.assertThat(result.responses().getFirst().category())
                        .isEqualTo(place1.getCategory());
                s.assertThat(result.responses().getFirst().markerCoordinate().latitude())
                        .isEqualTo(place1.getCoordinate().getLatitude());
                s.assertThat(result.responses().getFirst().markerCoordinate().longitude())
                        .isEqualTo(place1.getCoordinate().getLongitude());
                s.assertThat(result.responses().getLast().category())
                        .isEqualTo(place2.getCategory());
                s.assertThat(result.responses().getLast().markerCoordinate().latitude())
                        .isEqualTo(place2.getCoordinate().getLatitude());
                s.assertThat(result.responses().getLast().markerCoordinate().longitude())
                        .isEqualTo(place2.getCoordinate().getLongitude());
            });
        }
    }
}
