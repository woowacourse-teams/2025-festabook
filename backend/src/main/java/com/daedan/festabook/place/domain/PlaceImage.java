package com.daedan.festabook.place.domain;

import jakarta.persistence.Column;
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
public class PlaceImage implements Comparable<PlaceImage> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Place place;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Integer sequence;

    protected PlaceImage(
            Long id,
            Place place,
            String imageUrl,
            Integer sequence
    ) {
        this.id = id;
        this.place = place;
        this.imageUrl = imageUrl;
        this.sequence = sequence;
    }

    // TODO PlaceImage 최대 5개 검증 로직 구현
    public PlaceImage(
            Place place,
            String imageUrl,
            Integer sequence
    ) {
        this(
                null,
                place,
                imageUrl,
                sequence
        );
    }

    public void updateSequence(int sequence) {
        this.sequence = sequence;
    }

    @Override
    public int compareTo(PlaceImage otherPlaceImage) {
        return sequence.compareTo(otherPlaceImage.sequence);
    }
}
