package com.daedan.festabook.event.domain;

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
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.http.HttpStatus;

@Entity
@Getter
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE event_date SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventDate extends BaseEntity implements Comparable<EventDate> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Festival festival;

    @Column(nullable = false)
    private LocalDate date;

    public EventDate(
            Festival festival,
            LocalDate date
    ) {
        validateDate(date);

        this.festival = festival;
        this.date = date;
    }

    public void updateDate(LocalDate date) {
        validateDate(date);

        this.date = date;
    }

    public boolean isFestivalIdEqualTo(Long festivalId) {
        return this.getFestival().getId().equals(festivalId);
    }

    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new BusinessException("일정 날짜는 null일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public int compareTo(EventDate otherEventDate) {
        return this.date.compareTo(otherEventDate.date);
    }
}
