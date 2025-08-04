package com.daedan.festabook.organization.domain;

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
public class FestivalImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Integer sequence;

    protected FestivalImage(
            Long id,
            Organization organization,
            String imageUrl,
            Integer sequence
    ) {
        this.id = id;
        this.organization = organization;
        this.imageUrl = imageUrl;
        this.sequence = sequence;
    }

    public FestivalImage(
            Organization organization,
            String imageUrl,
            Integer sequence
    ) {
        this(
                null,
                organization,
                imageUrl,
                sequence
        );
    }
}
