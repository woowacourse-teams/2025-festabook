package com.daedan.festabook.festival.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lineup implements Comparable<Lineup> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Festival festival;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String imageUrl;

    // todo : 추후 event 테이블 조인 고려
    @Column(nullable = false)
    private LocalDateTime performanceAt;

    protected Lineup(
            Long id,
            Festival festival,
            String name,
            String imageUrl,
            LocalDateTime performanceAt
    ) {
        this.id = id;
        this.festival = festival;
        this.name = name;
        this.imageUrl = imageUrl;
        this.performanceAt = performanceAt;
    }

    public Lineup(
            Festival festival,
            String name,
            String imageUrl,
            LocalDateTime performanceAt
    ) {
        this(
                null,
                festival,
                name,
                imageUrl,
                performanceAt
        );
    }

    @Override
    public int compareTo(Lineup otherLineup) {
        return performanceAt.compareTo(otherLineup.performanceAt);
    }
}
