package com.daedan.festabook.event.domain;

import com.daedan.festabook.global.domain.BaseEntity;
import com.daedan.festabook.global.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Clock;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@Entity
@Getter
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE event SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseEntity implements Comparable<Event> {

    private static final int MAX_TITLE_LENGTH = 30;
    private static final int MAX_LOCATION_LENGTH = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EventDate eventDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(length = MAX_TITLE_LENGTH, nullable = false)
    private String title;

    @Column(length = MAX_LOCATION_LENGTH, nullable = false)
    private String location;

    public Event(
            EventDate eventDate,
            LocalTime startTime,
            LocalTime endTime,
            String title,
            String location
    ) {
        validateEventDate(eventDate);
        validateTimes(startTime, endTime);
        validateTitle(title);
        validateLocation(location);

        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.location = location;
    }

    public EventStatus determineStatus(Clock clock) {
        return EventStatus.determine(clock, eventDate.getDate(), startTime, endTime);
    }

    public void updateEvent(Event newEvent) {
        this.startTime = newEvent.startTime;
        this.endTime = newEvent.endTime;
        this.title = newEvent.title;
        this.location = newEvent.location;
    }

    public boolean isFestivalIdEqualTo(Long festivalId) {
        return this.eventDate.isFestivalIdEqualTo(festivalId);
    }

    private void validateEventDate(EventDate eventDate) {
        if (eventDate == null) {
            throw new BusinessException("일정 날짜는 null일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateTimes(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            throw new BusinessException("시작 시간과 종료 시간은 null일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateTitle(String title) {
        if (!StringUtils.hasText(title)) {
            throw new BusinessException("일정 제목은 공백이거나 null일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new BusinessException(
                    String.format("일정 제목의 길이는 %d자를 초과할 수 없습니다.", MAX_TITLE_LENGTH),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateLocation(String location) {
        if (!StringUtils.hasText(location)) {
            throw new BusinessException("일정 위치는 공백이거나 null일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        if (location.length() > MAX_LOCATION_LENGTH) {
            throw new BusinessException(
                    String.format("일정 위치의 길이는 %d자를 초과할 수 없습니다.", MAX_LOCATION_LENGTH),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @Override
    public int compareTo(Event otherEvent) {
        if (startTime.equals(otherEvent.startTime)) {
            return endTime.compareTo(otherEvent.endTime);
        }
        return startTime.compareTo(otherEvent.startTime);
    }
}
