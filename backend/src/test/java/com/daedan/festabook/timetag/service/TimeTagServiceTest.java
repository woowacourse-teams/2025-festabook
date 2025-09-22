package com.daedan.festabook.timetag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.timetag.domain.TimeTag;
import com.daedan.festabook.timetag.domain.TimeTagFixture;
import com.daedan.festabook.timetag.dto.TimeTagCreateRequest;
import com.daedan.festabook.timetag.dto.TimeTagCreateRequestFixture;
import com.daedan.festabook.timetag.dto.TimeTagCreateResponse;
import com.daedan.festabook.timetag.dto.TimeTagResponses;
import com.daedan.festabook.timetag.dto.TimeTagUpdateRequest;
import com.daedan.festabook.timetag.dto.TimeTagUpdateRequestFixture;
import com.daedan.festabook.timetag.dto.TimeTagUpdateResponse;
import com.daedan.festabook.timetag.infrastructure.PlaceTimeTagJpaRepository;
import com.daedan.festabook.timetag.infrastructure.TimeTagJpaRepository;
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
class TimeTagServiceTest {

    @Mock
    private FestivalJpaRepository festivalJpaRepository;

    @Mock
    private TimeTagJpaRepository timeTagJpaRepository;

    @Mock
    private PlaceTimeTagJpaRepository placeTimeTagJpaRepository;

    @InjectMocks
    private TimeTagService timeTagService;

    @Nested
    class createTimeTag {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            TimeTagCreateRequest request = TimeTagCreateRequestFixture.createDefault();

            given(festivalJpaRepository.findById(festivalId))
                    .willReturn(Optional.of(festival));
            given(timeTagJpaRepository.save(any(TimeTag.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            TimeTagCreateResponse response = timeTagService.createTimeTag(festivalId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(response.festivalId()).isEqualTo(festivalId);
                s.assertThat(response.name()).isEqualTo(request.name());
            });
        }

        @Test
        void 예외_존재하지_않는_축제() {
            // given
            Long invalidFestivalId = 0L;
            TimeTagCreateRequest request = TimeTagCreateRequestFixture.createDefault();

            // when & then
            assertThatThrownBy(() -> timeTagService.createTimeTag(invalidFestivalId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 축제입니다.");
        }
    }

    @Nested
    class getAllTimeTagsByFestivalId {

        @Test
        void 성공_응답_개수() {
            // given
            int expectedSize = 3;
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            List<TimeTag> timeTags = TimeTagFixture.createWithFestivalList(festival, expectedSize);

            given(timeTagJpaRepository.findAllByFestivalId(festivalId))
                    .willReturn(timeTags);

            // when
            TimeTagResponses response = timeTagService.getAllTimeTagsByFestivalId(festivalId);

            // then
            int responseSize = response.responses().size();
            assertThat(responseSize).isEqualTo(expectedSize);
        }

        @Test
        void 성공_필드_값() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Long timeTagId = 1L;
            TimeTag timeTag = TimeTagFixture.createWithFestivalAndId(festival, timeTagId);
            List<TimeTag> timeTags = List.of(timeTag);

            given(timeTagJpaRepository.findAllByFestivalId(festivalId))
                    .willReturn(timeTags);

            // when
            TimeTagResponses response = timeTagService.getAllTimeTagsByFestivalId(festivalId);

            // then
            int responseSize = response.responses().size();
            assertSoftly(s -> {
                s.assertThat(response.responses().get(0).timeTagId()).isEqualTo(timeTagId);
                s.assertThat(response.responses().get(0).name()).isEqualTo(timeTag.getName());
            });
        }
    }

    @Nested
    class updateTimeTag {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Long timeTagId = 1L;
            TimeTag timeTag = TimeTagFixture.createWithFestivalAndId(festival, timeTagId);

            given(timeTagJpaRepository.findById(timeTagId))
                    .willReturn(Optional.of(timeTag));

            TimeTagUpdateRequest request = TimeTagUpdateRequestFixture.createDefault();

            // when
            TimeTagUpdateResponse response = timeTagService.updateTimeTag(festivalId, timeTagId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(response.id()).isEqualTo(timeTagId);
                s.assertThat(response.festivalId()).isEqualTo(festivalId);
                s.assertThat(response.name()).isEqualTo(request.name());
            });
        }

        @Test
        void 예외_다른_축제_값_수정() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Long otherFestivalId = 999L;
            Long timeTagId = 1L;
            TimeTag timeTag = TimeTagFixture.createWithFestivalAndId(festival, timeTagId);

            given(timeTagJpaRepository.findById(timeTagId))
                    .willReturn(Optional.of(timeTag));

            TimeTagUpdateRequest request = TimeTagUpdateRequestFixture.createDefault();

            // when & then
            assertThatThrownBy(() -> timeTagService.updateTimeTag(otherFestivalId, timeTagId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 시간 태그가 아닙니다.");
        }

        @Test
        void 예외_존재하지_않는_시간_태그() {
            // given
            Long festivalId = 1L;
            Long invalidTimeTagId = 0L;
            TimeTagUpdateRequest request = TimeTagUpdateRequestFixture.createDefault();

            // when & then
            assertThatThrownBy(() -> timeTagService.updateTimeTag(festivalId, invalidTimeTagId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 시간 태그입니다.");
        }
    }

    @Nested
    class deleteTimeTag {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Long timeTagId = 1L;
            TimeTag timeTag = TimeTagFixture.createWithFestivalAndId(festival, timeTagId);

            given(timeTagJpaRepository.findById(timeTagId))
                    .willReturn(Optional.of(timeTag));

            // when
            timeTagService.deleteTimeTag(festivalId, timeTagId);

            // then
            then(timeTagJpaRepository).should()
                    .deleteById(timeTagId);
        }

        @Test
        void 예외_존재하지_않는_시간_태그() {
            // given
            Long festivalId = 1L;
            Long invalidTimeTagId = 0L;

            // when & then
            assertThatThrownBy(() -> timeTagService.deleteTimeTag(festivalId, invalidTimeTagId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 시간 태그입니다.");
        }

        @Test
        void 예외_다른_축제_값_삭제() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Long otherFestivalId = 999L;
            Long timeTagId = 1L;
            TimeTag timeTag = TimeTagFixture.createWithFestivalAndId(festival, timeTagId);

            given(timeTagJpaRepository.findById(timeTagId))
                    .willReturn(Optional.of(timeTag));

            // when & then
            assertThatThrownBy(() -> timeTagService.deleteTimeTag(otherFestivalId, timeTagId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 시간 태그가 아닙니다.");
        }

        @Test
        void 예외_사용중인_시간_태그_삭제() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Long timeTagId = 1L;
            TimeTag timeTag = TimeTagFixture.createWithFestivalAndId(festival, timeTagId);

            given(timeTagJpaRepository.findById(timeTagId))
                    .willReturn(Optional.of(timeTag));

            given(placeTimeTagJpaRepository.existsByTimeTag(any(TimeTag.class)))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> timeTagService.deleteTimeTag(festivalId, timeTagId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 시간 태그는 사용 중이므로 삭제할 수 없습니다.");
        }
    }
}
