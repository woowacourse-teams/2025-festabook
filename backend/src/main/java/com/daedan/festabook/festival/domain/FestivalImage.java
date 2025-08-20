package com.daedan.festabook.festival.domain;

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
public class FestivalImage extends BaseEntity implements Comparable<FestivalImage> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Festival festival;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Integer sequence;

    protected FestivalImage(
            Long id,
            Festival festival,
            String imageUrl,
            Integer sequence
    ) {
        this.id = id;
        this.festival = festival;
        this.imageUrl = imageUrl;
        this.sequence = sequence;
    }

    public FestivalImage(
            Festival festival,
            String imageUrl,
            Integer sequence
    ) {
        this(
                null,
                festival,
                imageUrl,
                sequence
        );
    }

    public void updateSequence(Integer sequence) {
        this.sequence = sequence;
    }

    @Override
    public int compareTo(FestivalImage otherFestivalImage) {
        return sequence.compareTo(otherFestivalImage.sequence);
    }
}
