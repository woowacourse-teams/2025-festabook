package com.daedan.festabook.festival.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.domain.FestivalImage;
import com.daedan.festabook.festival.domain.FestivalImageFixture;
import com.daedan.festabook.festival.dto.FestivalImageRequest;
import com.daedan.festabook.festival.dto.FestivalImageRequestFixture;
import com.daedan.festabook.festival.dto.FestivalImageResponse;
import com.daedan.festabook.festival.dto.FestivalImageResponses;
import com.daedan.festabook.festival.dto.FestivalImageSequenceUpdateRequest;
import com.daedan.festabook.festival.dto.FestivalImageSequenceUpdateRequestFixture;
import com.daedan.festabook.festival.infrastructure.FestivalImageJpaRepository;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
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
    private FestivalJpaRepository festivalJpaRepository;

    @Mock
    private FestivalImageJpaRepository festivalImageJpaRepository;

    @InjectMocks
    private FestivalImageService festivalImageService;

    @Nested
    class addFestivalImage {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            FestivalImageRequest request = FestivalImageRequestFixture.create();
            int currentSequence = 3;
            int expectedSequence = currentSequence + 1;

            given(festivalJpaRepository.findById(festivalId))
                    .willReturn(Optional.of(festival));
            given(festivalImageJpaRepository.findMaxSequenceByFestivalId(festivalId))
                    .willReturn(Optional.of(currentSequence));
            given(festivalImageJpaRepository.save(any()))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            FestivalImageResponse result = festivalImageService.addFestivalImage(festivalId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.imageUrl()).isEqualTo(request.imageUrl());
                s.assertThat(result.sequence()).isEqualTo(expectedSequence);
            });
        }

        @Test
        void 예외_존재하지_않는_축제() {
            // given
            Long invalidFestivalId = 0L;
            FestivalImageRequest request = FestivalImageRequestFixture.create();

            // when & then
            assertThatThrownBy(() -> festivalImageService.addFestivalImage(invalidFestivalId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 축제입니다.");
        }
    }

    @Nested
    class updateFestivalImagesSequence {

        @Test
        void 성공_수정_후_응답값_오름차순_정렬() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);

            Long festivalImageId1 = 1L;
            Long festivalImageId2 = 2L;
            Long festivalImageId3 = 3L;

            FestivalImage festivalImage1 = FestivalImageFixture.create(festival, 1, festivalImageId1);
            FestivalImage festivalImage2 = FestivalImageFixture.create(festival, 2, festivalImageId2);
            FestivalImage festivalImage3 = FestivalImageFixture.create(festival, 3, festivalImageId3);

            List<FestivalImageSequenceUpdateRequest> requests = List.of(
                    FestivalImageSequenceUpdateRequestFixture.create(festivalImageId1, 3),
                    FestivalImageSequenceUpdateRequestFixture.create(festivalImageId2, 2),
                    FestivalImageSequenceUpdateRequestFixture.create(festivalImageId3, 1)
            );

            given(festivalImageJpaRepository.findById(festivalImageId1))
                    .willReturn(Optional.of(festivalImage1));
            given(festivalImageJpaRepository.findById(festivalImageId2))
                    .willReturn(Optional.of(festivalImage2));
            given(festivalImageJpaRepository.findById(festivalImageId3))
                    .willReturn(Optional.of(festivalImage3));

            // when
            FestivalImageResponses result = festivalImageService.updateFestivalImagesSequence(festivalId, requests);

            // then
            assertSoftly(s -> {
                s.assertThat(result.responses().get(0).festivalImageId()).isEqualTo(festivalImageId3);
                s.assertThat(result.responses().get(0).sequence()).isEqualTo(1);

                s.assertThat(result.responses().get(1).festivalImageId()).isEqualTo(festivalImageId2);
                s.assertThat(result.responses().get(1).sequence()).isEqualTo(2);

                s.assertThat(result.responses().get(2).festivalImageId()).isEqualTo(festivalImageId1);
                s.assertThat(result.responses().get(2).sequence()).isEqualTo(3);
            });
        }

        @Test
        void 예외_존재하지_않는_축제_이미지() {
            // given
            Long festivalId = 1L;
            List<FestivalImageSequenceUpdateRequest> requests = FestivalImageSequenceUpdateRequestFixture.createList(3);

            // when & then
            assertThatThrownBy(() -> festivalImageService.updateFestivalImagesSequence(festivalId, requests))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 축제 이미지입니다.");
        }

        @Test
        void 예외_다른_축제의_축제_이미지일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Long festivalImageId = 1L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            FestivalImage festivalImage = FestivalImageFixture.create(requestFestival, festivalImageId);

            given(festivalImageJpaRepository.findById(festivalImage.getId()))
                    .willReturn(Optional.of(festivalImage));

            List<FestivalImageSequenceUpdateRequest> requests = FestivalImageSequenceUpdateRequestFixture.createList(3);

            // when & then
            assertThatThrownBy(() -> festivalImageService.updateFestivalImagesSequence(otherFestival.getId(), requests))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 축제 이미지가 아닙니다.");
        }
    }

    @Nested
    class removeFestivalImage {

        @Test
        void 성공() {
            // given
            Long festivalImageId = 1L;
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            FestivalImage festivalImage = FestivalImageFixture.create(festival);

            given(festivalImageJpaRepository.findById(festivalImageId))
                    .willReturn(Optional.of(festivalImage));

            // when
            festivalImageService.removeFestivalImage(festivalId, festivalImageId);

            // then
            then(festivalImageJpaRepository).should()
                    .deleteById(festivalImageId);
        }

        @Test
        void 예외_다른_축제의_축제_이미지일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Long festivalImageId = 1L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            FestivalImage festivalImage = FestivalImageFixture.create(requestFestival, festivalImageId);

            given(festivalImageJpaRepository.findById(festivalImage.getId()))
                    .willReturn(Optional.of(festivalImage));

            // when & then
            assertThatThrownBy(() ->
                    festivalImageService.removeFestivalImage(otherFestival.getId(), festivalImage.getId())
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 축제 이미지가 아닙니다.");
        }
    }
}
