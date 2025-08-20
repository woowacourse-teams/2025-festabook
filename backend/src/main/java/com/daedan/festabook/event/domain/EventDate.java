package com.daedan.festabook.event.domain;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.global.domain.BaseEntity;
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
public class EventDate extends BaseEntity implements Comparable<EventDate> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Festival festival;

    @Column(nullable = false)
    private LocalDate date;

    public EventDate(
            Festival festival,
            LocalDate date
    ) {
        this.festival = festival;
        this.date = date;
    }

    public void updateDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public int compareTo(EventDate otherEventDate) {
        return this.date.compareTo(otherEventDate.date);
    }
}
