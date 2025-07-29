package com.daedan.festabook.place.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Place place;
    
    private String title;

    private String description;

    private String location;

    private String host;

    private LocalTime startTime;

    private LocalTime endTime;

    protected PlaceDetail(
            Long id,
            Place place,
            String title,
            String description,
            String location,
            String host,
            LocalTime startTime,
            LocalTime endTime
    ) {
        this.id = id;
        this.place = place;
        this.title = title;
        this.description = description;
        this.location = location;
        this.host = host;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public PlaceDetail(
            Place place,
            String title,
            String description,
            String location,
            String host,
            LocalTime startTime,
            LocalTime endTime
    ) {
        this(
                null,
                place,
                title,
                description,
                location,
                host,
                startTime,
                endTime
        );
    }

    public PlaceDetail(
            Place place,
            String title
    ) {
        this(
                null,
                place,
                title,
                null,
                null,
                null,
                null,
                null
        );
    }
}
