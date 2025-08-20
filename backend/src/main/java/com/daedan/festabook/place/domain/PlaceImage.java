package com.daedan.festabook.place.domain;

import com.daedan.festabook.global.domain.BaseEntity;
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
public class PlaceImage extends BaseEntity implements Comparable<PlaceImage> {

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

    public PlaceImage(
            Place place,
            String imageUrl,
            Integer sequence
    ) {
        this.place = place;
        this.imageUrl = imageUrl;
        this.sequence = sequence;
    }

    public void updateSequence(int sequence) {
        this.sequence = sequence;
    }

    @Override
    public int compareTo(PlaceImage otherPlaceImage) {
        return sequence.compareTo(otherPlaceImage.sequence);
    }
}
