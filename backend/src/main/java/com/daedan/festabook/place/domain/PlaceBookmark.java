package com.daedan.festabook.place.domain;

import com.daedan.festabook.device.domain.Device;
import jakarta.persistence.Entity;
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
public class PlaceBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Place place;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Device device;

    protected PlaceBookmark(
            Long id,
            Place place,
            Device device
    ) {
        this.id = id;
        this.place = place;
        this.device = device;
    }

    public PlaceBookmark(
            Place place,
            Device device
    ) {
        this(
                null,
                place,
                device
        );
    }
}
