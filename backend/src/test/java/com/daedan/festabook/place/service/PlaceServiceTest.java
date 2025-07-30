package com.daedan.festabook.place.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceAnnouncementFixture;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceDetail;
import com.daedan.festabook.place.domain.PlaceDetailFixture;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.domain.PlaceImageFixture;
import com.daedan.festabook.place.dto.PlaceRequest;
import com.daedan.festabook.place.dto.PlaceRequestFixture;
import com.daedan.festabook.place.dto.PlaceResponse;
import com.daedan.festabook.place.dto.PlaceResponses;
import com.daedan.festabook.place.dto.PlaceUpdateRequest;
import com.daedan.festabook.place.dto.PlaceUpdateRequestFixture;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceDetailJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceFavoriteJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
import java.time.LocalTime;
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
class PlaceServiceTest {

    @Mock
    private PlaceJpaRepository placeJpaRepository;

    @Mock
    private PlaceImageJpaRepository placeImageJpaRepository;

    @Mock
    private PlaceDetailJpaRepository placeDetailJpaRepository;

    @Mock
    private OrganizationJpaRepository organizationJpaRepository;

    @Mock
    private PlaceFavoriteJpaRepository placeFavoriteJpaRepository;

    @Mock
    private PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    @InjectMocks
    private PlaceService placeService;

    @Nested
    class createPlace {

        @Test
        void 성공() {
            // given
            Long organizationId = 1L;
            Long expectedPlaceId = 1L;
            PlaceCategory expectedPlaceCategory = PlaceCategory.BAR;
            PlaceRequest placeRequest = PlaceRequestFixture.create(expectedPlaceCategory);

            Organization organization = OrganizationFixture.create(organizationId);
            Place place = PlaceFixture.createWithNullDefaults(expectedPlaceId, organization, expectedPlaceCategory);

            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(organization));
            given(placeJpaRepository.save(any()))
                    .willReturn(place);

            // when
            PlaceResponse result = placeService.createPlace(organizationId, placeRequest);

            // then
            assertSoftly(s -> {
                s.assertThat(result.id()).isEqualTo(expectedPlaceId);
                s.assertThat(result.category()).isEqualTo(expectedPlaceCategory);

                s.assertThat(result.placeImages().responses()).isEmpty();
                s.assertThat(result.placeAnnouncements().responses()).isEmpty();

                s.assertThat(result.title()).isNull();
                s.assertThat(result.startTime()).isNull();
                s.assertThat(result.endTime()).isNull();
                s.assertThat(result.location()).isNull();
                s.assertThat(result.host()).isNull();
                s.assertThat(result.description()).isNull();
            });
        }

        @Test
        void 예외_존재하지_않는_조직() {
            // given
            Long invalidOrganizationId = 0L;

            PlaceRequest placeRequest = PlaceRequestFixture.create();

            given(organizationJpaRepository.findById(invalidOrganizationId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> placeService.createPlace(invalidOrganizationId, placeRequest))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 조직입니다.");

            then(placeJpaRepository).should(never())
                    .save(any());
        }
    }

    @Nested
    class getAllPlaceByOrganizationId {

        @Test
        void 성공_PlaceDetail이_있는_경우() {
            // given
            Long organizationId = 1L;

            Organization organization = OrganizationFixture.create(organizationId);
            Place place = PlaceFixture.create(organization);

            PlaceDetail detail = PlaceDetailFixture.create(place);

            PlaceImage image = PlaceImageFixture.create(place);
            PlaceAnnouncement announcement = PlaceAnnouncementFixture.create(place);

            given(placeJpaRepository.findAllByOrganizationId(organizationId))
                    .willReturn(List.of(place));
            given(placeDetailJpaRepository.existsByPlace(place))
                    .willReturn(true);
            given(placeDetailJpaRepository.findByPlaceId(place.getId()))
                    .willReturn(Optional.of(detail));
            given(placeImageJpaRepository.findAllByPlaceIdOrderBySequenceAsc(place.getId()))
                    .willReturn(List.of(image));
            given(placeAnnouncementJpaRepository.findAllByPlaceId(place.getId()))
                    .willReturn(List.of(announcement));

            // when
            PlaceResponses result = placeService.getAllPlaceByOrganizationId(organizationId);

            // then
            assertThat(result.responses()).hasSize(1);
        }

        @Test
        void 성공_PlaceDetail이_없는_경우() {
            // given
            Long organizationId = 1L;

            Organization organization = OrganizationFixture.create(organizationId);
            Place place = PlaceFixture.create(organization);

            given(placeJpaRepository.findAllByOrganizationId(organizationId))
                    .willReturn(List.of(place));
            given(placeDetailJpaRepository.existsByPlace(place))
                    .willReturn(false);

            // when
            PlaceResponses result = placeService.getAllPlaceByOrganizationId(organizationId);

            // then
            assertThat(result.responses()).hasSize(1);
        }
    }

    @Nested
    class getPlaceWithDetailByPlaceId {

        @Test
        void 성공() {
            // given
            Long expectedPlaceId = 1L;

            Place place = PlaceFixture.create(expectedPlaceId);

            PlaceDetail placeDetail = PlaceDetailFixture.create(place);

            PlaceImage image1 = PlaceImageFixture.create(place);
            PlaceImage image2 = PlaceImageFixture.create(place);

            PlaceAnnouncement announcement1 = PlaceAnnouncementFixture.create(place);
            PlaceAnnouncement announcement2 = PlaceAnnouncementFixture.create(place);

            given(placeJpaRepository.findById(expectedPlaceId))
                    .willReturn(Optional.of(place));
            given(placeDetailJpaRepository.existsByPlace(place))
                    .willReturn(true);
            given(placeDetailJpaRepository.findByPlaceId(expectedPlaceId))
                    .willReturn(Optional.of(placeDetail));
            given(placeImageJpaRepository.findAllByPlaceIdOrderBySequenceAsc(expectedPlaceId))
                    .willReturn(List.of(image1, image2));
            given(placeAnnouncementJpaRepository.findAllByPlaceId(expectedPlaceId))
                    .willReturn(List.of(announcement1, announcement2));

            // when
            PlaceResponse result = placeService.getPlaceWithDetailByPlaceId(expectedPlaceId);

            // then
            assertSoftly(s -> {
                s.assertThat(result.id()).isEqualTo(expectedPlaceId);
                s.assertThat(result.category()).isEqualTo(place.getCategory());

                s.assertThat(result.description()).isEqualTo(placeDetail.getDescription());
                s.assertThat(result.host()).isEqualTo(placeDetail.getHost());
                s.assertThat(result.location()).isEqualTo(placeDetail.getLocation());
                s.assertThat(result.endTime()).isEqualTo(placeDetail.getEndTime());
                s.assertThat(result.startTime()).isEqualTo(placeDetail.getStartTime());
                s.assertThat(result.title()).isEqualTo(placeDetail.getTitle());

                s.assertThat(result.placeImages().responses()).hasSize(2);
                s.assertThat(result.placeAnnouncements().responses()).hasSize(2);
            });
        }

        @Test
        void 성공_PlaceDetail이_없는_경우() {
            // given
            Long expectedPlaceId = 1L;

            Place place = PlaceFixture.create(expectedPlaceId);

            given(placeJpaRepository.findById(expectedPlaceId))
                    .willReturn(Optional.of(place));
            given(placeDetailJpaRepository.existsByPlace(place))
                    .willReturn(false);

            // when
            PlaceResponse result = placeService.getPlaceWithDetailByPlaceId(expectedPlaceId);

            // then
            assertSoftly(s -> {
                s.assertThat(result.id()).isEqualTo(expectedPlaceId);
                s.assertThat(result.category()).isEqualTo(place.getCategory());

                s.assertThat(result.description()).isNull();
                s.assertThat(result.host()).isNull();
                s.assertThat(result.location()).isNull();
                s.assertThat(result.endTime()).isNull();
                s.assertThat(result.startTime()).isNull();
                s.assertThat(result.title()).isNull();

                s.assertThat(result.placeImages().responses()).isEmpty();
                s.assertThat(result.placeAnnouncements().responses()).isEmpty();
            });
        }

        @Test
        void 예외_존재하지_않는_place_id() {
            // given
            Long inValidPlaceId = 0L;

            // when & then
            assertThatThrownBy(() -> placeService.getPlaceWithDetailByPlaceId(inValidPlaceId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 플레이스입니다.");
        }
    }

    @Nested
    class updatePlace {

        @Test
        void 성공() {
            // given
            Long placeId = 1L;
            PlaceCategory before = PlaceCategory.BAR;
            Place place = PlaceFixture.create(placeId, before);
            PlaceDetail placeDetail = new PlaceDetail(
                    place,
                    "이름",
                    "설명",
                    "위치",
                    "호스트",
                    LocalTime.of(10, 30),
                    LocalTime.of(10, 30)
            );

            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            given(placeDetailJpaRepository.findByPlaceId(placeId))
                    .willReturn(Optional.of(placeDetail));

            PlaceUpdateRequest placeUpdateRequest = new PlaceUpdateRequest(
                    PlaceCategory.FOOD_TRUCK,
                    "새로운 이름",
                    "새로운 설명",
                    "새로운 위치",
                    "새로운 호스트",
                    LocalTime.of(12, 30),
                    LocalTime.of(13, 00)
            );

            // when
            placeService.updatePlace(placeId, placeUpdateRequest);

            // then
            assertSoftly(s -> {
                s.assertThat(place.getCategory()).isEqualTo(placeUpdateRequest.placeCategory());

                s.assertThat(placeDetail.getTitle()).isEqualTo(placeUpdateRequest.title());
                s.assertThat(placeDetail.getDescription()).isEqualTo(placeUpdateRequest.description());
                s.assertThat(placeDetail.getLocation()).isEqualTo(placeUpdateRequest.location());
                s.assertThat(placeDetail.getHost()).isEqualTo(placeUpdateRequest.host());
                s.assertThat(placeDetail.getStartTime()).isEqualTo(placeUpdateRequest.startTime());
                s.assertThat(placeDetail.getEndTime()).isEqualTo(placeUpdateRequest.endTime());
            });
        }

        @Test
        void 성공_PlaceDetail_없다면_새로_저장() {
            // given
            Long placeId = 1L;
            PlaceCategory before = PlaceCategory.BAR;
            Place place = PlaceFixture.create(placeId, before);

            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            given(placeDetailJpaRepository.findByPlaceId(placeId))
                    .willReturn(Optional.empty());

            PlaceUpdateRequest placeUpdateRequest = new PlaceUpdateRequest(
                    PlaceCategory.FOOD_TRUCK,
                    "새로운 이름",
                    "새로운 설명",
                    "새로운 위치",
                    "새로운 호스트",
                    LocalTime.of(12, 30),
                    LocalTime.of(13, 00)
            );

            // when
            placeService.updatePlace(placeId, placeUpdateRequest);

            // then
            then(placeDetailJpaRepository).should()
                    .save(any());
        }

        @Test
        void 실패_플레이스가_존재하지_않는다면_예외가_발생() {
            // given
            Long invalidPlaceId = 0L;

            PlaceUpdateRequest placeUpdateRequest = PlaceUpdateRequestFixture.create();

            // when & then
            assertThatThrownBy(() -> placeService.updatePlace(invalidPlaceId, placeUpdateRequest))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 플레이스입니다.");
        }
    }

    @Nested
    class deleteByPlaceId {

        @Test
        void 성공() {
            // given
            Long expectedPlaceId = 1L;

            // when
            placeService.deleteByPlaceId(expectedPlaceId);

            // then
            then(placeDetailJpaRepository).should()
                    .deleteByPlaceId(expectedPlaceId);
            then(placeImageJpaRepository).should()
                    .deleteAllByPlaceId(expectedPlaceId);
            then(placeAnnouncementJpaRepository).should()
                    .deleteAllByPlaceId(expectedPlaceId);
            then(placeFavoriteJpaRepository).should()
                    .deleteAllByPlaceId(expectedPlaceId);
            then(placeJpaRepository).should()
                    .deleteById(expectedPlaceId);
        }
    }
}
