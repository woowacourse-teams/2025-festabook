package com.daedan.festabook.lineup.domain;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.global.domain.BaseEntity;
import com.daedan.festabook.global.exception.BusinessException;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@Entity
@Getter
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE lineup SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lineup extends BaseEntity implements Comparable<Lineup> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Festival festival;

    @Column(nullable = false)
    private String name;

    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime performanceAt;

    public Lineup(
            Festival festival,
            String name,
            String imageUrl,
            LocalDateTime performanceAt
    ) {
        validateName(name);

        this.festival = festival;
        this.name = name;
        this.imageUrl = imageUrl;
        this.performanceAt = performanceAt;
    }

    public void updateLineup(String name, String imageUrl, LocalDateTime performanceAt) {
        validateName(name);

        this.name = name;
        this.imageUrl = imageUrl;
        this.performanceAt = performanceAt;
    }

    public boolean isFestivalIdEqualTo(Long festivalId) {
        return this.getFestival().getId().equals(festivalId);
    }

    private void validateName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new BusinessException("이름은 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public int compareTo(Lineup otherLineup) {
        return performanceAt.compareTo(otherLineup.performanceAt);
    }
}
