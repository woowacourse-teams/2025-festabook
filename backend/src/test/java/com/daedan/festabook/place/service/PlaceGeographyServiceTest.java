package com.daedan.festabook.place.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceCoordinateRequestFixture;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.dto.PlaceCoordinateRequest;
import com.daedan.festabook.place.dto.PlaceCoordinateResponse;
import com.daedan.festabook.place.dto.PlaceGeographyResponses;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import java.util.List;
import java.util.Optional;
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

            Place place1 = PlaceFixture.create(organization, PlaceCategory.BAR, 37.123, 125.432);
            Place place2 = PlaceFixture.create(organization, PlaceCategory.SMOKING, 37.343, 125.782);

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

        @Test
        void 성공_Coordinate가_없을_경우_응답에_포함하지_않음() {
            // given
            Organization organization = OrganizationFixture.create(1L);

            Place place = PlaceFixture.create(organization, PlaceCategory.BAR, null);

            given(placeJpaRepository.findAllByOrganizationId(organization.getId()))
                    .willReturn(List.of(place));

            // when
            PlaceGeographyResponses result = placeGeographyService.getAllPlaceGeographyByOrganizationId(
                    organization.getId()
            );

            // then
            assertThat(result.responses()).isEmpty();
        }
    }

    @Nested
    class updatePlaceCoordinate {

        @Test
        void 성공() {
            // given
            Long placeId = 1L;
            Organization organization = OrganizationFixture.create(1L);
            Place place = PlaceFixture.create(placeId, organization, 37.0, 127.0);
            PlaceCoordinateRequest request = PlaceCoordinateRequestFixture.create();

            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(place));

            // when
            PlaceCoordinateResponse result = placeGeographyService.updatePlaceCoordinate(placeId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.id()).isEqualTo(placeId);
                s.assertThat(result.coordinate().latitude()).isEqualTo(request.latitude());
                s.assertThat(result.coordinate().longitude()).isEqualTo(request.longitude());
            });
        }

        @Test
        void 예외_존재하지_않는_플레이스() {
            // given
            Long placeId = 1L;
            PlaceCoordinateRequest request = PlaceCoordinateRequestFixture.create();

            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> placeGeographyService.updatePlaceCoordinate(placeId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 플레이스입니다.");
        }
    }
}
