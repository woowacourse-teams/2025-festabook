package com.daedan.festabook.place.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceAnnouncement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Place place;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public PlaceAnnouncement(
            Place place,
            String title,
            String content
    ) {
        this.place = place;
        this.title = title;
        this.content = content;
    }
}
