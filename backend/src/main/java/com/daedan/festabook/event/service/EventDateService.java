package com.daedan.festabook.event.service;

import com.daedan.festabook.event.domain.EventDate;
import com.daedan.festabook.event.dto.EventDateRequest;
import com.daedan.festabook.event.dto.EventDateResponse;
import com.daedan.festabook.event.dto.EventDateResponses;
import com.daedan.festabook.event.dto.EventDateUpdateRequest;
import com.daedan.festabook.event.dto.EventDateUpdateResponse;
import com.daedan.festabook.event.infrastructure.EventDateJpaRepository;
import com.daedan.festabook.event.infrastructure.EventJpaRepository;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventDateService {

    private final EventDateJpaRepository eventDateJpaRepository;
    private final EventJpaRepository eventJpaRepository;
    private final FestivalJpaRepository festivalJpaRepository;

    @Transactional
    public EventDateResponse createEventDate(Long festivalId, EventDateRequest request) {
        validateDuplicatedEventDate(festivalId, request.date());
        Festival festival = getFestivalById(festivalId);
        EventDate eventDate = request.toEntity(festival);

        EventDate savedEventDate = eventDateJpaRepository.save(eventDate);
        return EventDateResponse.from(savedEventDate);
    }

    public EventDateResponses getAllEventDateByFestivalId(Long festivalId) {
        List<EventDate> eventDates = eventDateJpaRepository.findAllByFestivalId(festivalId).stream()
                .sorted()
                .toList();
        return EventDateResponses.from(eventDates);
    }

    @Transactional
    public EventDateUpdateResponse updateEventDate(Long eventDateId, Long festivalId, EventDateUpdateRequest request) {
        EventDate eventDate = getEventDateById(eventDateId);
        validateEventDateBelongsToFestival(eventDate, festivalId);
        if (!eventDate.getDate().isEqual(request.date())) {
            validateDuplicatedEventDate(festivalId, request.date());
        }

        eventDate.updateDate(request.date());
        return EventDateUpdateResponse.from(eventDate);
    }

    @Transactional
    public void deleteEventDateByEventDateId(Long eventDateId, Long festivalId) {
        EventDate eventDate = getEventDateById(eventDateId);
        validateEventDateBelongsToFestival(eventDate, festivalId);

        eventJpaRepository.deleteAllByEventDateId(eventDateId);
        eventDateJpaRepository.deleteById(eventDateId);
    }

    private Festival getFestivalById(Long festivalId) {
        return festivalJpaRepository.findById(festivalId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 축제입니다.", HttpStatus.BAD_REQUEST));
    }

    private EventDate getEventDateById(Long eventDateId) {
        return eventDateJpaRepository.findById(eventDateId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 일정 날짜입니다.", HttpStatus.BAD_REQUEST));
    }

    private void validateDuplicatedEventDate(Long festivalId, LocalDate date) {
        if (eventDateJpaRepository.existsByFestivalIdAndDate(festivalId, date)) {
            throw new BusinessException("이미 존재하는 일정 날짜입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateEventDateBelongsToFestival(EventDate eventDate, Long festivalId) {
        if (!eventDate.isFestivalIdEqualTo(festivalId)) {
            throw new BusinessException("해당 축제의 일정 날짜가 아닙니다.", HttpStatus.FORBIDDEN);
        }
    }
}
