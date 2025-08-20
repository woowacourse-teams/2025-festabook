package com.daedan.festabook.announcement.domain;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.global.exception.BusinessException;
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
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@Entity
@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Announcement {

    private static final int MAX_TITLE_LENGTH = 50;
    private static final int MAX_CONTENT_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Festival festival;

    @Column(nullable = false, length = MAX_TITLE_LENGTH)
    private String title;

    @Column(nullable = false, length = MAX_CONTENT_LENGTH)
    private String content;

    @Column(nullable = false)
    private boolean isPinned;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Announcement(
            Long id,
            String title,
            String content,
            boolean isPinned,
            Festival festival,
            LocalDateTime createdAt
    ) {
        validateTitle(title);
        validateContent(content);
        validateFestival(festival);

        this.id = id;
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;
        this.festival = festival;
        this.createdAt = createdAt;
    }

    public Announcement(
            String title,
            String content,
            boolean isPinned,
            Festival festival
    ) {
        this(null, title, content, isPinned, festival, null);
    }

    public boolean isUnpinned() {
        return !isPinned;
    }

    public void updateTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void updatePinned(boolean isPinned) {
        this.isPinned = isPinned;
    }

    private void validateTitle(String title) {
        if (!StringUtils.hasText(title)) {
            throw new BusinessException("공지사항 제목은 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        if (title.length() > MAX_TITLE_LENGTH) {
            throw new BusinessException(
                    String.format("공지사항 제목은 %s자를 초과할 수 없습니다.", MAX_TITLE_LENGTH),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateContent(String content) {
        if (!StringUtils.hasText(content)) {
            throw new BusinessException("공지사항 본문은 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException(
                    String.format("공지사항 본문은 %s자를 초과할 수 없습니다.", MAX_CONTENT_LENGTH),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateFestival(Festival festival) {
        if (festival == null) {
            throw new BusinessException("축제는 null일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
