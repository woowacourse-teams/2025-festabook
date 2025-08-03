package com.daedan.festabook.organization.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.FestivalImage;
import com.daedan.festabook.organization.domain.FestivalImageFixture;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.dto.FestivalImageDeleteRequest;
import com.daedan.festabook.organization.dto.FestivalImageDeleteRequestFixture;
import com.daedan.festabook.organization.dto.FestivalImageRequest;
import com.daedan.festabook.organization.dto.FestivalImageRequestFixture;
import com.daedan.festabook.organization.dto.FestivalImageResponse;
import com.daedan.festabook.organization.dto.FestivalImageResponses;
import com.daedan.festabook.organization.dto.FestivalImageSequenceUpdateRequest;
import com.daedan.festabook.organization.dto.FestivalImageSequenceUpdateRequestFixture;
import com.daedan.festabook.organization.infrastructure.FestivalImageJpaRepository;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
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
class FestivalImageServiceTest {

    @Mock
    private OrganizationJpaRepository organizationJpaRepository;

    @Mock
    private FestivalImageJpaRepository festivalImageJpaRepository;

    @InjectMocks
    private FestivalImageService festivalImageService;

    @Nested
    class addFestivalImage {

        @Test
        void 성공() {
            // given
            Long organizationId = 1L;
            Organization organization = OrganizationFixture.create(organizationId);
            FestivalImageRequest request = FestivalImageRequestFixture.create();
            int currentSequence = 3;

            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(organization));
            given(festivalImageJpaRepository.countByOrganizationId(organizationId))
                    .willReturn(currentSequence);
            given(festivalImageJpaRepository.save(any()))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            FestivalImageResponse result = festivalImageService.addFestivalImage(organizationId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.imageUrl()).isEqualTo(request.imageUrl());
                s.assertThat(result.sequence()).isEqualTo(currentSequence + 1);
            });
        }

        @Test
        void 예외_존재하지_않는_조직() {
            // given
            Long invalidOrganizationId = 0L;
            FestivalImageRequest request = FestivalImageRequestFixture.create();

            // when & then
            assertThatThrownBy(
                    () -> festivalImageService.addFestivalImage(invalidOrganizationId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 조직입니다.");
        }
    }

    @Nested
    class updateFestivalImagesSequence {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();

            Long festivalImage1Id = 1L;
            Long festivalImage2Id = 2L;
            Long festivalImage3Id = 3L;

            FestivalImage festivalImage1 = FestivalImageFixture.create(festivalImage1Id, organization, 1);
            FestivalImage festivalImage2 = FestivalImageFixture.create(festivalImage2Id, organization, 2);
            FestivalImage festivalImage3 = FestivalImageFixture.create(festivalImage3Id, organization, 3);

            List<FestivalImageSequenceUpdateRequest> requests = List.of(
                    FestivalImageSequenceUpdateRequestFixture.create(festivalImage1Id, 2),
                    FestivalImageSequenceUpdateRequestFixture.create(festivalImage2Id, 3),
                    FestivalImageSequenceUpdateRequestFixture.create(festivalImage3Id, 1)
            );

            given(festivalImageJpaRepository.findById(festivalImage1Id))
                    .willReturn(Optional.of(festivalImage1));
            given(festivalImageJpaRepository.findById(festivalImage2Id))
                    .willReturn(Optional.of(festivalImage2));
            given(festivalImageJpaRepository.findById(festivalImage3Id))
                    .willReturn(Optional.of(festivalImage3));

            // when
            FestivalImageResponses result = festivalImageService.updateFestivalImagesSequence(
                    organization.getId(),
                    requests
            );

            // then
            assertSoftly(s -> {
                s.assertThat(result.responses().get(0).festivalImageId()).isEqualTo(festivalImage3Id);
                s.assertThat(result.responses().get(0).sequence()).isEqualTo(1);

                s.assertThat(result.responses().get(1).festivalImageId()).isEqualTo(festivalImage1Id);
                s.assertThat(result.responses().get(1).sequence()).isEqualTo(2);

                s.assertThat(result.responses().get(2).festivalImageId()).isEqualTo(festivalImage2Id);
                s.assertThat(result.responses().get(2).sequence()).isEqualTo(3);
            });
        }

        @Test
        void 예외_권한이_없는_축제_이미지() {
            // given
            Organization organization = OrganizationFixture.create(1L);
            Organization anotherOrganization = OrganizationFixture.create(2L);

            Long unauthorizedImageId = 1L;
            FestivalImage unauthorizedImage = FestivalImageFixture.create(unauthorizedImageId, anotherOrganization, 1);

            List<FestivalImageSequenceUpdateRequest> requests = List.of(
                    FestivalImageSequenceUpdateRequestFixture.create(unauthorizedImageId, 1)
            );

            given(festivalImageJpaRepository.findAllByOrganizationId(organization.getId()))
                    .willReturn(List.of());
            given(festivalImageJpaRepository.findById(unauthorizedImageId))
                    .willReturn(Optional.of(unauthorizedImage));

            // when & then
            assertThatThrownBy(() -> festivalImageService.updateFestivalImagesSequence(organization.getId(), requests))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("권한이 없습니다.");
        }

        @Test
        void 예외_존재하지_않는_축제_이미지() {
            // given
            Long organizationId = 1L;
            List<FestivalImageSequenceUpdateRequest> requests = FestivalImageSequenceUpdateRequestFixture.createList(3);

            // when & then
            assertThatThrownBy(() -> festivalImageService.updateFestivalImagesSequence(organizationId, requests))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 축제 이미지입니다.");
        }
    }

    @Nested
    class removeFestivalImages {

        @Test
        void 성공() {
            // given
            Long organizationId = 1L;

            Long festivalImageId1 = 1L;
            Long festivalImageId2 = 2L;
            Long festivalImageId3 = 3L;

            FestivalImageDeleteRequest request1 = FestivalImageDeleteRequestFixture.create(festivalImageId1);
            FestivalImageDeleteRequest request2 = FestivalImageDeleteRequestFixture.create(festivalImageId2);
            FestivalImageDeleteRequest request3 = FestivalImageDeleteRequestFixture.create(festivalImageId3);
            List<FestivalImageDeleteRequest> requests = List.of(request1, request2, request3);

            // when
            festivalImageService.removeFestivalImages(organizationId, requests);

            // then
            then(festivalImageJpaRepository).should()
                    .deleteAllById(List.of(festivalImageId1, festivalImageId2, festivalImageId3));
        }

        @Test
        void 예외_권한이_없는_축제_이미지() {
            // given
            Long organizationId = 1L;
            Long anotherOrganizationId = 999L;
            Organization organization = OrganizationFixture.create(organizationId);
            FestivalImage festivalImage = FestivalImageFixture.create(organization);

            given(festivalImageJpaRepository.findAllByOrganizationId(organizationId))
                    .willReturn(List.of(festivalImage));

            List<FestivalImageDeleteRequest> requests = List.of(
                    FestivalImageDeleteRequestFixture.create(organizationId),
                    FestivalImageDeleteRequestFixture.create(anotherOrganizationId)
            );

            // when & then
            assertThatThrownBy(() -> festivalImageService.removeFestivalImages(organizationId, requests))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("권한이 없습니다.");
        }
    }
}
