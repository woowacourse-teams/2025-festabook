package com.daedan.festabook.schedule.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.schedule.domain.EventDate;
import com.daedan.festabook.schedule.dto.EventDateRequest;
import com.daedan.festabook.schedule.dto.EventDateResponse;
import com.daedan.festabook.schedule.dto.EventDateResponses;
import com.daedan.festabook.schedule.infrastructure.EventDateJpaRepository;
import com.daedan.festabook.schedule.infrastructure.EventJpaRepository;
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
    private final OrganizationJpaRepository organizationJpaRepository;

    public EventDateResponse createEventDate(Long organizationId, EventDateRequest request) {
        validateDuplicatedEventDate(organizationId, request.date());

        Organization organization = getOrganizationById(organizationId);
        EventDate eventDate = request.toEntity(organization);
        EventDate savedEventDate = eventDateJpaRepository.save(eventDate);

        return EventDateResponse.from(savedEventDate);
    }

    @Transactional
    public void deleteEventDateByEventDateId(Long eventDateId) {
        eventJpaRepository.deleteAllByEventDateId(eventDateId);
        eventDateJpaRepository.deleteById(eventDateId);
    }

    public EventDateResponses getAllEventDateByOrganizationId(Long organizationId) {
        List<EventDate> eventDates = eventDateJpaRepository.findAllByOrganizationId(organizationId).stream()
                .sorted()
                .toList();
        return EventDateResponses.from(eventDates);
    }

    private Organization getOrganizationById(Long organizationId) {
        return organizationJpaRepository.findById(organizationId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 조직입니다.", HttpStatus.BAD_REQUEST));
    }

    private void validateDuplicatedEventDate(Long organizationId, LocalDate date) {
        if (eventDateJpaRepository.existsByOrganizationIdAndDate(organizationId, date)) {
            throw new BusinessException("이미 존재하는 일정 날짜입니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
