package com.daedan.festabook.festival.domain;

import com.daedan.festabook.global.exception.BusinessException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coordinate {

    private static final double MIN_LATITUDE = -90;
    private static final double MAX_LATITUDE = 90;
    private static final double MIN_LONGITUDE = -180;
    private static final double MAX_LONGITUDE = 180;

    private Double latitude;

    private Double longitude;

    public Coordinate(
            Double latitude,
            Double longitude
    ) {
        validateLatitude(latitude);
        validateLongitude(longitude);

        this.latitude = latitude;
        this.longitude = longitude;
    }

    private void validateLatitude(Double latitude) {
        if (latitude == null) {
            throw new BusinessException("위도는 null일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE) {
            throw new BusinessException(
                    String.format("위도는 %.1f도 이상 %.1f도 이하여야 합니다.", MIN_LATITUDE, MAX_LATITUDE),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateLongitude(Double longitude) {
        if (longitude == null) {
            throw new BusinessException("경도는 null일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        if (longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new BusinessException(
                    String.format("경도는 %.1f도 이상 %.1f도 이하여야 합니다.", MIN_LONGITUDE, MAX_LONGITUDE),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
