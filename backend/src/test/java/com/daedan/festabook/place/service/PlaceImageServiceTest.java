package com.daedan.festabook.place.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.domain.PlaceImageFixture;
import com.daedan.festabook.place.dto.PlaceImageRequest;
import com.daedan.festabook.place.dto.PlaceImageRequestFixture;
import com.daedan.festabook.place.dto.PlaceImageResponse;
import com.daedan.festabook.place.dto.PlaceImageSequenceUpdateRequest;
import com.daedan.festabook.place.dto.PlaceImageSequenceUpdateRequestFixture;
import com.daedan.festabook.place.dto.PlaceImageSequenceUpdateResponses;
import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
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
public class PlaceImageServiceTest {

    private static final Long MAX_IMAGE_COUNT = 5L;

    @Mock
    private PlaceJpaRepository placeJpaRepository;

    @Mock
    private PlaceImageJpaRepository placeImageJpaRepository;

    @InjectMocks
    private PlaceImageService placeImageService;

    @Nested
    class addPlaceImage {

        @Test
        void 성공() {
            // given
            Long placeId = 1L;
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Place place = PlaceFixture.create(placeId, festival);

            int maxSequence = 1;
            int newSequence = maxSequence + 1;

            Long savePlaceImageId = 2L;
            PlaceImage savedPlaceImage = PlaceImageFixture.create(savePlaceImageId, place, newSequence);

            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            given(placeImageJpaRepository.findMaxSequenceByPlace(place))
                    .willReturn(Optional.of(maxSequence));
            given(placeImageJpaRepository.save(any()))
                    .willReturn(savedPlaceImage);

            PlaceImageRequest request = new PlaceImageRequest(savedPlaceImage.getImageUrl());

            // when
            PlaceImageResponse result = placeImageService.addPlaceImage(placeId, festivalId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.id()).isEqualTo(savePlaceImageId);
                s.assertThat(result.imageUrl()).isEqualTo(savedPlaceImage.getImageUrl());
                s.assertThat(result.sequence()).isEqualTo(newSequence);
            });
        }

        @Test
        void 예외_존재하지_않는_플레이스() {
            // given
            Long invalidPlaceId = 1L;
            Long festivalId = 1L;

            given(placeJpaRepository.findById(invalidPlaceId))
                    .willReturn(Optional.empty());

            PlaceImageRequest request = PlaceImageRequestFixture.create();

            // when & then
            assertThatThrownBy(() -> placeImageService.addPlaceImage(invalidPlaceId, festivalId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 플레이스입니다.");
        }

        @Test
        void 예외_최대_이미지_수_초과() {
            // given
            Long placeId = 1L;
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Place place = PlaceFixture.create(placeId, festival);

            int sequence = 10;
            Long imageCount = MAX_IMAGE_COUNT + 1;

            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            given(placeImageJpaRepository.findMaxSequenceByPlace(place))
                    .willReturn(Optional.of(sequence));
            given(placeImageJpaRepository.countByPlace(place))
                    .willReturn(imageCount);

            PlaceImageRequest request = PlaceImageRequestFixture.create();

            // when & then
            assertThatThrownBy(() -> placeImageService.addPlaceImage(placeId, festivalId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(String.format("플레이스 이미지는 최대 %d개까지 저장할 수 있습니다.", MAX_IMAGE_COUNT));
        }

        @Test
        void 예외_다른_축제의_플레이스일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Long placeId = 1L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            Place place = PlaceFixture.create(placeId, requestFestival);

            given(placeJpaRepository.findById(placeId))
                    .willReturn(Optional.of(place));

            PlaceImageRequest request = PlaceImageRequestFixture.create();

            // when & then
            assertThatThrownBy(() -> placeImageService.addPlaceImage(place.getId(), otherFestival.getId(), request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 플레이스가 아닙니다.");
        }
    }

    @Nested
    class updatePlaceImagesSequence {

        @Test
        void 성공_수정_후_응답값_오름차순_정렬() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Place place = PlaceFixture.create(festival);

            Long placeImageId1 = 1L;
            Long placeImageId2 = 2L;
            Long placeImageId3 = 3L;

            PlaceImage placeImage1 = PlaceImageFixture.create(placeImageId1, place, 1);
            PlaceImage placeImage2 = PlaceImageFixture.create(placeImageId2, place, 2);
            PlaceImage placeImage3 = PlaceImageFixture.create(placeImageId3, place, 3);

            List<PlaceImageSequenceUpdateRequest> requests = List.of(
                    PlaceImageSequenceUpdateRequestFixture.create(placeImageId1, 3),
                    PlaceImageSequenceUpdateRequestFixture.create(placeImageId2, 2),
                    PlaceImageSequenceUpdateRequestFixture.create(placeImageId3, 1)
            );

            given(placeImageJpaRepository.findById(placeImageId1))
                    .willReturn(Optional.of(placeImage1));
            given(placeImageJpaRepository.findById(placeImageId2))
                    .willReturn(Optional.of(placeImage2));
            given(placeImageJpaRepository.findById(placeImageId3))
                    .willReturn(Optional.of(placeImage3));

            // when
            PlaceImageSequenceUpdateResponses result = placeImageService.updatePlaceImagesSequence(
                    festival.getId(),
                    requests
            );

            // then
            assertSoftly(s -> {
                s.assertThat(result.responses().get(0).placeImageId()).isEqualTo(placeImageId3);
                s.assertThat(result.responses().get(0).sequence()).isEqualTo(1);

                s.assertThat(result.responses().get(1).placeImageId()).isEqualTo(placeImageId2);
                s.assertThat(result.responses().get(1).sequence()).isEqualTo(2);

                s.assertThat(result.responses().get(2).placeImageId()).isEqualTo(placeImageId1);
                s.assertThat(result.responses().get(2).sequence()).isEqualTo(3);
            });
        }

        @Test
        void 예외_존재하지_않는_플레이스_이미지() {
            // given
            Long festivalId = 1L;
            List<PlaceImageSequenceUpdateRequest> requests = PlaceImageSequenceUpdateRequestFixture.createList(1);

            // when & then
            assertThatThrownBy(() -> placeImageService.updatePlaceImagesSequence(festivalId, requests))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 플레이스 이미지입니다.");
        }

        @Test
        void 예외_다른_축제의_플레이스_이미지일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Long placeImageId = 1L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            Place place = PlaceFixture.create(requestFestival);
            PlaceImage placeImage = PlaceImageFixture.create(placeImageId, place);

            given(placeImageJpaRepository.findById(placeImage.getId()))
                    .willReturn(Optional.of(placeImage));

            List<PlaceImageSequenceUpdateRequest> requests = PlaceImageSequenceUpdateRequestFixture.createList(1);

            // when & then
            assertThatThrownBy(() -> placeImageService.updatePlaceImagesSequence(otherFestival.getId(), requests))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 플레이스 이미지가 아닙니다.");
        }
    }

    @Nested
    class deletePlaceImageByPlaceImageId {

        @Test
        void 성공() {
            // given
            Long placeImageId = 1L;
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Place place = PlaceFixture.create(festival);
            PlaceImage placeImage = PlaceImageFixture.create(placeImageId, place);

            given(placeImageJpaRepository.findById(placeImageId))
                    .willReturn(Optional.of(placeImage));

            // when
            placeImageService.deletePlaceImageByPlaceImageId(placeImageId, festivalId);

            // then
            then(placeImageJpaRepository).should()
                    .deleteById(placeImageId);
        }

        @Test
        void 예외_다른_축제의_플레이스_이미지일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Long placeImageId = 1L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            Place place = PlaceFixture.create(requestFestival);
            PlaceImage placeImage = PlaceImageFixture.create(placeImageId, place);

            given(placeImageJpaRepository.findById(placeImageId))
                    .willReturn(Optional.of(placeImage));

            // when & then
            assertThatThrownBy(() ->
                    placeImageService.deletePlaceImageByPlaceImageId(placeImage.getId(), otherFestival.getId())
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 플레이스 이미지가 아닙니다.");
        }
    }
}
