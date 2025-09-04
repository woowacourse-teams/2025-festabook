package com.daedan.festabook.place.domain;

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
@SQLDelete(sql = "UPDATE place_announcement SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceAnnouncement extends BaseEntity {

    private static final int MAX_TITLE_LENGTH = 20;
    private static final int MAX_CONTENT_LENGTH = 250;

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

    public PlaceAnnouncement(
            Place place,
            String title,
            String content
    ) {
        validateTitle(title);
        validateContent(content);

        this.place = place;
        this.title = title;
        this.content = content;
    }

    public void updatePlaceAnnouncement(String title, String content) {
        validateTitle(title);
        validateContent(content);

        this.title = title;
        this.content = content;
    }

    public boolean isFestivalIdEqualTo(Long festivalId) {
        return this.place.isFestivalIdEqualTo(festivalId);
    }

    private void validateTitle(String title) {
        if (!StringUtils.hasText(title)) {
            throw new BusinessException("플레이스 공지의 제목은 비어있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        if (title.length() > MAX_TITLE_LENGTH) {
            throw new BusinessException(
                    String.format("플레이스 공지 제목의 길이는 %d자를 초과할 수 없습니다.", MAX_TITLE_LENGTH),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateContent(String content) {
        if (content == null) {
            throw new BusinessException("플레이스 공지 내용은 null일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException(
                    String.format("플레이스 공지 내용의 길이는 %d자를 초과할 수 없습니다.", MAX_CONTENT_LENGTH),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
