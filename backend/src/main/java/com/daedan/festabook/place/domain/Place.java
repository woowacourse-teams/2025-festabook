package com.daedan.festabook.place.domain;

import com.daedan.festabook.festival.domain.Coordinate;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.global.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalTime;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    private static final Set<PlaceCategory> MAIN_PLACE = Set.of(
            PlaceCategory.BAR,
            PlaceCategory.BOOTH,
            PlaceCategory.FOOD_TRUCK
    );

    private static final int MAX_TITLE_LENGTH = 20;
    private static final int MAX_DESCRIPTION_LENGTH = 100;
    private static final int MAX_LOCATION_LENGTH = 100;
    private static final int MAX_HOST_LENGTH = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Festival festival;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlaceCategory category;

    @Column(length = 20)
    private String title;

    @Column(length = 100)
    private String description;

    @Column(length = 100)
    private String location;

    @Column(length = 100)
    private String host;

    private LocalTime startTime;

    private LocalTime endTime;

    @Embedded
    private Coordinate coordinate;

    protected Place(
            Long id,
            Festival festival,
            PlaceCategory category,
            Coordinate coordinate,
            String title,
            String description,
            String location,
            String host,
            LocalTime startTime,
            LocalTime endTime
    ) {
        validateTitle(title);
        validateDescription(description);
        validateLocation(location);
        validateHost(host);
        validateTime(startTime, endTime);

        this.id = id;
        this.festival = festival;
        this.category = category;
        this.coordinate = coordinate;
        this.title = title;
        this.description = description;
        this.location = location;
        this.host = host;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Place(
            Festival festival,
            PlaceCategory category,
            Coordinate coordinate
    ) {
        this(
                null,
                festival,
                category,
                coordinate,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public boolean isMainPlace() {
        return MAIN_PLACE.contains(this.category);
    }

    public void updateCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    private void validateTitle(String title) {
        if (title == null) {
            return;
        }

        if (title.trim().isEmpty()) {
            throw new BusinessException("플레이스의 이름은 공백일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        if (title.length() > MAX_TITLE_LENGTH) {
            throw new BusinessException(
                    String.format("플레이스의 이름의 길이는 %d자를 초과할 수 없습니다.", MAX_TITLE_LENGTH),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateDescription(String description) {
        if (description == null) {
            return;
        }

        if (description.trim().isEmpty()) {
            throw new BusinessException("플레이스의 설명은 공백일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new BusinessException(
                    String.format("플레이스 설명의 길이는 %d자를 초과할 수 없습니다.", MAX_DESCRIPTION_LENGTH),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateLocation(String location) {
        if (location == null) {
            return;
        }

        if (location.trim().isEmpty()) {
            throw new BusinessException("플레이스의 위치는 공백일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        if (location.length() > MAX_LOCATION_LENGTH) {
            throw new BusinessException(
                    String.format("플레이스 위치의 길이는 %d자를 초과할 수 없습니다.", MAX_LOCATION_LENGTH),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateHost(String host) {
        if (host == null) {
            return;
        }

        if (host.trim().isEmpty()) {
            throw new BusinessException("플레이스의 호스트는 공백일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        if (host.length() > MAX_HOST_LENGTH) {
            throw new BusinessException(
                    String.format("플레이스 호스트의 길이는 %d자를 초과할 수 없습니다.", MAX_HOST_LENGTH),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateTime(LocalTime startTime, LocalTime endTime) {
        if (startTime == null && endTime == null) {
            return;
        }

        if (startTime == null || endTime == null) {
            throw new BusinessException("플레이스의 시작 날짜, 종료 날짜는 모두 비어 있거나 모두 입력되어야 합니다.",
                    HttpStatus.BAD_REQUEST);
        }
    }
}
