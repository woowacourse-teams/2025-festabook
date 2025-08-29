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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE festival_image SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
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

    public FestivalImage(
            Festival festival,
            String imageUrl,
            Integer sequence
    ) {
        this.festival = festival;
        this.imageUrl = imageUrl;
        this.sequence = sequence;
    }

    public void updateSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public boolean isFestivalIdEqualTo(Long festivalId) {
        return this.getFestival().getId().equals(festivalId);
    }

    @Override
    public int compareTo(FestivalImage otherFestivalImage) {
        return sequence.compareTo(otherFestivalImage.sequence);
    }
}
