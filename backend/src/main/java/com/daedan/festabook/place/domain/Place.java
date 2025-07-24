package com.daedan.festabook.place.domain;

import com.daedan.festabook.organization.domain.Coordinate;
import com.daedan.festabook.organization.domain.Organization;
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
    private Organization organization;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlaceCategory category;

    @Embedded
    private Coordinate coordinate;

    protected Place(
            Long id,
            Organization organization,
            PlaceCategory category,
            Coordinate coordinate
    ) {
        this.id = id;
        this.organization = organization;
        this.category = category;
        this.coordinate = coordinate;
    }

    public Place(
            Organization organization,
            PlaceCategory category,
            Coordinate coordinate
    ) {
        this(
                null,
                organization,
                category,
                coordinate
        );
    }

    public Place(
            Organization organization,
            PlaceCategory category
    ) {
        this(
                null,
                organization,
                category,
                null
        );
    }

    public boolean hasDetail() {
        return category.isServiceLocation();
    }
}
