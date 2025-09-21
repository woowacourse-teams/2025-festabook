package com.daedan.festabook.timetag.domain;

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
@SQLDelete(sql = "UPDATE time_tag SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeTag extends BaseEntity {

    private static final int MAX_NAME_LENGTH = 40;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Festival festival;

    @Column(length = MAX_NAME_LENGTH, nullable = false)
    private String name;

    public TimeTag(Festival festival, String name) {
        validateName(name);

        this.festival = festival;
        this.name = name;
    }

    private void validateName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new BusinessException("시간 태그의 이름은 공백일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        if (name.length() > MAX_NAME_LENGTH) {
            throw new BusinessException(
                    String.format("시간 태그의 이름의 길이는 %d자를 초과할 수 없습니다.", MAX_NAME_LENGTH),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public boolean isFestivalIdEqualTo(Long festivalId) {
        return this.festival.getId().equals(festivalId);
    }

    public void updateTimeTag(TimeTag newTimeTag) {
        this.name = newTimeTag.getName();
    }
}
