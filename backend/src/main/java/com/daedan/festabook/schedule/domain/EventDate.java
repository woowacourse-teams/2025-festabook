package com.daedan.festabook.schedule.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventDate implements Comparable<EventDate> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long organizationId;

    @Column(nullable = false)
    private LocalDate date;

    public EventDate(
            Long organizationId,
            LocalDate date
    ) {
        this.organizationId = organizationId;
        this.date = date;
    }

    @Override
    public int compareTo(EventDate otherEventDate) {
        return this.date.compareTo(otherEventDate.date);
    }
}
