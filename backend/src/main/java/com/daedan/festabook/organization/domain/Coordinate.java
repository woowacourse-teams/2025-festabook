package com.daedan.festabook.organization.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coordinate {

    private Double latitude;

    private Double longitude;

    public Coordinate(
            Double latitude,
            Double longitude
    ) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
