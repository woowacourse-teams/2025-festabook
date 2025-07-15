package com.daedan.festabook.schedule.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event implements Comparable<Event> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Column(nullable = false)
    private LocalTime startTime;

    private LocalTime endTime;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String location;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EventDate eventDate;

    public Event(
            EventStatus status,
            LocalTime startTime,
            LocalTime endTime,
            String title,
            String location,
            EventDate eventDate
    ) {
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.location = location;
        this.eventDate = eventDate;
    }

    @Override
    public int compareTo(Event otherEvent) {
        if (startTime.equals(otherEvent.startTime)) {
            return endTime.compareTo(otherEvent.endTime);
        }
        return startTime.compareTo(otherEvent.startTime);
    }
}
