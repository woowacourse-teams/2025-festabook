package com.daedan.festabook.timetag.service;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.timetag.domain.TimeTag;
import com.daedan.festabook.timetag.dto.TimeTagCreateRequest;
import com.daedan.festabook.timetag.dto.TimeTagCreateResponse;
import com.daedan.festabook.timetag.dto.TimeTagResponses;
import com.daedan.festabook.timetag.dto.TimeTagUpdateRequest;
import com.daedan.festabook.timetag.dto.TimeTagUpdateResponse;
import com.daedan.festabook.timetag.infrastructure.PlaceTimeTagJpaRepository;
import com.daedan.festabook.timetag.infrastructure.TimeTagJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TimeTagService {

    private final FestivalJpaRepository festivalJpaRepository;
    private final TimeTagJpaRepository timeTagJpaRepository;
    private final PlaceTimeTagJpaRepository placeTimeTagJpaRepository;

    @Transactional
    public TimeTagCreateResponse createTimeTag(Long festivalId, TimeTagCreateRequest request) {
        Festival festival = getFestivalById(festivalId);

        TimeTag timeTag = new TimeTag(festival, request.name());
        TimeTag savedTimeTag = timeTagJpaRepository.save(timeTag);

        return TimeTagCreateResponse.from(savedTimeTag);
    }

    @Transactional(readOnly = true)
    public TimeTagResponses getAllTimeTagsByFestivalId(Long festivalId) {
        List<TimeTag> timeTags = timeTagJpaRepository.findAllByFestivalId(festivalId);
        return TimeTagResponses.from(timeTags);
    }

    @Transactional
    public TimeTagUpdateResponse updateTimeTag(Long festivalId, Long timeTagId, TimeTagUpdateRequest request) {
        TimeTag timeTag = getTimeTagById(timeTagId);
        validateTimeTagBelongsToFestival(timeTag, festivalId);
        TimeTag newTimeTag = new TimeTag(timeTag.getFestival(), request.name());

        timeTag.updateTimeTag(newTimeTag);
        return TimeTagUpdateResponse.from(timeTag);
    }

    @Transactional
    public void deleteTimeTag(Long festivalId, Long timeTagId) {
        TimeTag timeTag = getTimeTagById(timeTagId);
        validateTimeTagBelongsToFestival(timeTag, festivalId);
        validateTimeTagNotInUse(timeTag);

        timeTagJpaRepository.deleteById(timeTagId);
    }

    private Festival getFestivalById(Long festivalId) {
        return festivalJpaRepository.findById(festivalId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 축제입니다.", HttpStatus.BAD_REQUEST));
    }

    private TimeTag getTimeTagById(Long timeTagId) {
        return timeTagJpaRepository.findById(timeTagId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 시간 태그입니다.", HttpStatus.NOT_FOUND));
    }

    private void validateTimeTagBelongsToFestival(TimeTag timeTag, Long festivalId) {
        if (!timeTag.isFestivalIdEqualTo(festivalId)) {
            throw new BusinessException("해당 축제의 시간 태그가 아닙니다.", HttpStatus.FORBIDDEN);
        }
    }

    private void validateTimeTagNotInUse(TimeTag timeTag) {
        if (placeTimeTagJpaRepository.existsByTimeTag(timeTag)) {
            throw new BusinessException("해당 시간 태그는 사용 중이므로 삭제할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
