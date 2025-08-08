package com.daedan.festabook.place.domain;

import com.daedan.festabook.festival.domain.Coordinate;
import com.daedan.festabook.festival.domain.Festival;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Festival festival;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlaceCategory category;

    @Embedded
    private Coordinate coordinate;

    protected Place(
            Long id,
            Festival festival,
            PlaceCategory category,
            Coordinate coordinate
    ) {
        this.id = id;
        this.festival = festival;
        this.category = category;
        this.coordinate = coordinate;
    }

    public Place(
            Festival festival,
            PlaceCategory category,
            Coordinate coordinate
    ) {
        this(
                null,
                festival,
                category,
                coordinate
        );
    }

    public boolean hasDetail() {
        return category.isServiceLocation();
    }

    public void updateCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }
}
