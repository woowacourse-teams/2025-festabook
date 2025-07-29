package com.daedan.festabook.event.domain;

import com.daedan.festabook.organization.domain.Organization;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;

    @Column(nullable = false)
    private LocalDate date;

    protected EventDate(
            Long id,
            Organization organization,
            LocalDate date
    ) {
        this.id = id;
        this.organization = organization;
        this.date = date;
    }

    public EventDate(
            Organization organization,
            LocalDate date
    ) {
        this(
                null,
                organization,
                date
        );
    }

    @Override
    public int compareTo(EventDate otherEventDate) {
        return this.date.compareTo(otherEventDate.date);
    }
}
