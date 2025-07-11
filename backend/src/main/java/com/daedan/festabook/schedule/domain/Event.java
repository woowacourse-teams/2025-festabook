package com.daedan.festabook.schedule.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {

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

    public Event(EventStatus status, LocalTime startTime, LocalTime endTime, String title, String location) {
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.location = location;
    }
}
