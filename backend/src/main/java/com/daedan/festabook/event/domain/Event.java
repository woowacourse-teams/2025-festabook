package com.daedan.festabook.event.domain;

import com.daedan.festabook.global.domain.BaseEntity;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseEntity implements Comparable<Event> {

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

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String location;

    protected Event(
            Long id,
            EventDate eventDate,
            LocalTime startTime,
            LocalTime endTime,
            String title,
            String location
    ) {
        this.id = id;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.location = location;
    }

    public Event(
            EventDate eventDate,
            LocalTime startTime,
            LocalTime endTime,
            String title,
            String location
    ) {
        this(
                null,
                eventDate,
                startTime,
                endTime,
                title,
                location
        );
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

    @Override
    public int compareTo(Event otherEvent) {
        if (startTime.equals(otherEvent.startTime)) {
            return endTime.compareTo(otherEvent.endTime);
        }
        return startTime.compareTo(otherEvent.startTime);
    }
}
