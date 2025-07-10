package com.daedan.festabook.schedule.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventDay implements Comparable<EventDay> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @OneToMany
    @JoinColumn(name = "event_id")
    private List<Event> events = new ArrayList<>();

    @Override
    public int compareTo(EventDay otherEventDay) {
        return this.date.compareTo(otherEventDay.date);
    }

    public EventDay(LocalDate date, List<Event> events) {
        this.id = null;
        this.date = date;
        this.events = events;
    }
}
